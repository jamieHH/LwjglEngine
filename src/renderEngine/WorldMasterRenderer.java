package renderEngine;

import entities.Entity;
import entities.Light;
import entities.Point;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import world.World;

import java.util.*;

public class WorldMasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 2000f;
    private static Matrix4f PROJECTION_MATRIX;

    private EntityRenderer entityRenderer;
    private StaticShader entityShader = new StaticShader();
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private NormalMappingRenderer normalMapRenderer;

    private SkyboxRenderer skyboxRenderer;

    private World world;
    private List<Terrain> terrains = new ArrayList<>();
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private Map<TexturedModel, List<Entity>> normalMappedEntities = new HashMap<>();
    private List<Light> lights = new ArrayList<>();

    public WorldMasterRenderer(World world) {
        this.world = world;
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(entityShader, PROJECTION_MATRIX);
        terrainRenderer = new TerrainRenderer(terrainShader, PROJECTION_MATRIX);
        skyboxRenderer = new SkyboxRenderer(PROJECTION_MATRIX);
        normalMapRenderer = new NormalMappingRenderer(PROJECTION_MATRIX);
    }

    public void render(Point camera) {
        prepare();
        entityShader.start();
        entityShader.loadViewMatrix(camera);
        entityShader.loadSkyColor(world.getSkyColor());
        entityShader.loadEnvLights(world.getEnvLights());
        entityShader.loadLights(lights);
        entityRenderer.render(entities);
        entityShader.stop();

        normalMapRenderer.render(normalMappedEntities, lights, camera);

        terrainShader.start();
        terrainShader.loadViewMatrix(camera);
        terrainShader.loadSkyColor(world.getSkyColor());
        terrainShader.loadEnvLights(world.getEnvLights());
        terrainShader.loadLights(lights);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        skyboxRenderer.render(camera, world.getSkyColor());
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(world.getSkyR(), world.getSkyG(), world.getSkyB(), 1);
    }

    private static void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        PROJECTION_MATRIX = new Matrix4f();
        PROJECTION_MATRIX.m00 = x_scale;
        PROJECTION_MATRIX.m11 = y_scale;
        PROJECTION_MATRIX.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        PROJECTION_MATRIX.m23 = -1;
        PROJECTION_MATRIX.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        PROJECTION_MATRIX.m33 = 0;
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMappedEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMappedEntities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    private void processLights(Point focus, int n) {
        List<Light> lights = new ArrayList<>(world.getLightsInSquare(focus.getPosition(), 300, 300));
        lights.sort((o1, o2) -> {
            if (o1.distanceTo(focus) == o2.distanceTo(focus)) {
                return 0;
            }
            return o1.distanceTo(focus) < o2.distanceTo(focus) ? -1 : 1;
        });
        if (lights.size() >= n) {
            lights = lights.subList(0, n - 1);
        }
        this.lights =  lights;
    }

    public void processWorld(Point focus) {
        processTerrain(world.getTerrain());
        for (Entity entity : world.getEntitiesInSquare(focus.getPosition(), 300, 300)) {
            if (entity.getModel().getTexture().isHasNormalMap()) {
                processNormalMapEntity(entity);
            } else {
                processEntity(entity);
            }
        }
        processLights(focus, 8);
    }

    public void clearProcessedWorld() {
        entities.clear();
        normalMappedEntities.clear();
        terrains.clear();
    }

    public void cleanUp() {
        entityShader.cleanUp();
        terrainShader.cleanUp();
        normalMapRenderer.cleanUp();
    }

    public static Matrix4f getProjectionMatrix() {
        return PROJECTION_MATRIX;
    }
}
