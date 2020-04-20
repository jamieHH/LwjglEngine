package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Point;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;
import world.World;

import java.util.*;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private World world;

    private Matrix4f projectionMatrix;

    private EntityRenderer entityRenderer;
    private StaticShader entityShader = new StaticShader();
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private List<Terrain> terrains = new ArrayList<>();
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Light> lights = new ArrayList<>();

    public MasterRenderer(World world) {
        this.world = world;
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void render(Camera camera) {
        prepare();
        entityShader.start();
        entityShader.loadSkyColor(world.getSkyR(), world.getSkyG(), world.getSkyB());
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        entityShader.stop();

        terrainShader.start();
        terrainShader.loadSkyColor(world.getSkyR(), world.getSkyG(), world.getSkyB());
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        entities.clear();
        terrains.clear();
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(world.getSkyR(), world.getSkyG(), world.getSkyB(), 1);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
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

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    private void processLights(Point camera, int n) {
        List<Light> lights = new ArrayList<>(world.getLights());
        lights.sort((o1, o2) -> {
            if (o1.distanceTo(camera) == o2.distanceTo(camera)) {
                return 0;
            }
            return o1.distanceTo(camera) < o2.distanceTo(camera) ? -1 : 1;
        });
        if (lights.size() >= n) {
            lights = lights.subList(0, n - 2);
        }
        lights.add(world.getEnvLight());
        this.lights =  lights;
    }

    public void processWorld(Point camera) {
        processTerrain(world.getTerrain());
        for (Entity entity : world.getEntities()) {
            processEntity(entity);
        }
        processLights(camera, 8);
    }

    public void cleanUp() {
        entityShader.cleanUp();
        terrainShader.cleanUp();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }
}
