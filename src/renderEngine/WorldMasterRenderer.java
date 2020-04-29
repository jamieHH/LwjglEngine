package renderEngine;

import entities.Entity;
import entities.Light;
import entities.Point;
import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import particles.Particle;
import particles.ParticleEmitter;
import particles.ParticleRenderer;
import particles.ParticleTexture;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import world.World;

import java.util.*;

public class WorldMasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 2000f;
    private static Matrix4f PROJECTION_MATRIX;

    private static EntityRenderer entityRenderer;
    private static TerrainRenderer terrainRenderer;
    private static NormalMappingRenderer normalMapRenderer;
    private static SkyboxRenderer skyboxRenderer;
    private static ParticleRenderer particleRenderer;

    private static World world;
    private static List<Terrain> terrains = new ArrayList<>();
    private static Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private static Map<TexturedModel, List<Entity>> normalMappedEntities = new HashMap<>();
    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static List<Light> lights = new ArrayList<>();

    public static void init(World world) {
        WorldMasterRenderer.world = world;
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(PROJECTION_MATRIX);
        terrainRenderer = new TerrainRenderer(PROJECTION_MATRIX);
        skyboxRenderer = new SkyboxRenderer(PROJECTION_MATRIX);
        normalMapRenderer = new NormalMappingRenderer(PROJECTION_MATRIX);
        particleRenderer = new ParticleRenderer(PROJECTION_MATRIX);
    }

    public static void render(Point camera) {
        WorldMasterRenderer.clearProcessedWorld();
        WorldMasterRenderer.processWorld(camera);
        prepare(camera);
        entityRenderer.render(entities, lights, world.getEnvLights(), world.getSkyColor(), camera);
        normalMapRenderer.render(normalMappedEntities, lights, world.getEnvLights(), world.getSkyColor(), camera);
        terrainRenderer.render(terrains, lights, world.getEnvLights(), world.getSkyColor(), camera);
        skyboxRenderer.render(camera, world.getSkyColor());
        particleRenderer.render(particles, camera);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private static void prepare(Point camera) {
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

    private static void addEntity(Entity entity) {
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

    private static void addNormalMappedEntity(Entity entity) {
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

    private static void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    private static void processLights(Point focus, int n) {
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
        WorldMasterRenderer.lights =  lights;
    }

    private static void processEntities(Point focus) {
        for (Entity entity : world.getEntitiesInSquare(focus.getPosition(), 300, 300)) {
            if (entity.getModel().getTexture().isHasNormalMap()) {
                addNormalMappedEntity(entity);
            } else {
                addEntity(entity);
            }
        }
    }

    public static void processWorld(Point focus) {
        processTerrain(world.getTerrain());
        processEntities(focus);
        processLights(focus, 16);
        processParticles(world.getParticleEmitters(), focus);
    }

    public static void processParticles(List<ParticleEmitter> emitters, Point camera) {
        for (ParticleEmitter emitter : emitters) {
            emitter.getParticles().sort((o1, o2) -> {
                if (o1.distanceTo(camera) == o2.distanceTo(camera)) {
                    return 0;
                }
                return o1.distanceTo(camera) < o2.distanceTo(camera) ? 1 : -1;
            });
            for (Particle particle : emitter.getParticles()) {
                addParticle(particle);
            }
        }
    }

    public static void addParticle(Particle particle) {
        List<Particle> list = particles.get(particle.getTexture());
        if (list == null) {
            list = new ArrayList<>();
            particles.put(particle.getTexture(), list);
        }
        list.add(particle);
    }

    public static void clearProcessedWorld() {
        entities.clear();
        normalMappedEntities.clear();
        terrains.clear();
        particles.clear();
    }

    public static void cleanUp() {
        entityRenderer.cleanUp();
        terrainRenderer.cleanUp();
        skyboxRenderer.cleanUp();
        normalMapRenderer.cleanUp();
        particleRenderer.cleanUp();
    }

    public static Matrix4f getProjectionMatrix() {
        return PROJECTION_MATRIX;
    }
}
