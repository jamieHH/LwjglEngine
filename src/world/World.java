package world;

import entities.Entity;
import entities.EnvLight;
import entities.Light;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleEmitter;
import terrains.Terrain;
import toolbox.sortAndPrune.Box;
import toolbox.sortAndPrune.SortAndPrune;

import java.util.ArrayList;
import java.util.List;

public class World {

    private Vector3f skyColor = new Vector3f(0.1f, 0.2f, 0.2f);
    private float gravity = -0.025f;

    private List<EnvLight> envLights = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    private List<Light> lights = new ArrayList<>();
    private List<ParticleEmitter> particleEmitters = new ArrayList<>();
    private Terrain terrain;

    private SortAndPrune sap = new SortAndPrune();

    public World() {

    }

    public void tick() {
        for (Entity e : entities) {
            if (e.isHasMoved()) {
                e.setHasMoved(false);
                sap.removeBoxId(e.getBoundingBox().id);
                sap.addBox(e.getBoundingBox());
            }
        }
        sap.update();
    }

    public List<Integer> findWorldIntersects(Box b) {
        return sap.findFullIntersects(b.id);
    }

    public float getSkyR() {
        return skyColor.getX();
    }

    public float getSkyG() {
        return skyColor.getX();
    }

    public float getSkyB() {
        return skyColor.getX();
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void addEntity(Entity entity, float posX, float posY, float posZ) {
        entity.setPosition(new Vector3f(posX, posY, posZ));
        entity.setWorld(this);
        entities.add(entity);
        sap.addBox(entity.getBoundingBox());
    }

    public void addEntity(Entity entity, float posX, float posY, float posZ, float rotX, float rotY, float rotZ) {
        entity.setPosition(new Vector3f(posX, posY, posZ));
        entity.setRotation(new Vector3f(rotX, rotY, rotZ));
        entity.setWorld(this);
        entities.add(entity);
        sap.addBox(entity.getBoundingBox());
    }

    public void addLight(Light light, float posX, float posY, float posZ) {
        light.setPosition(new Vector3f(posX, posY, posZ));
        light.setWorld(this);
        lights.add(light);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public void addEnvLight(EnvLight envLight) {
        envLights.add(envLight);
    }

    public List<EnvLight> getEnvLights() {
        return envLights;
    }

    public Vector3f getSkyColor() {
        return skyColor;
    }

    public List<ParticleEmitter> getParticleEmitters() {
        return particleEmitters;
    }

    public void addParticleEmitter(ParticleEmitter particleEmitter, float posX, float posY, float posZ) {
        particleEmitter.setPosition(new Vector3f(posX, posY, posZ));
        particleEmitter.setWorld(this);
        this.particleEmitters.add(particleEmitter);
    }

    //---- Helpers
    public List<Entity> getEntitiesInSquare(float posX0, float posZ0, float posX1, float posZ1) {
        List<Entity> entities = new ArrayList<>();
        for (Entity e : getEntities()) {
            if (e.isInside(posX0, posZ0, posX1, posZ1)) {
                entities.add(e);
            }
        }

        return entities;
    }

    public List<Entity> getEntitiesInSquare(Vector3f position, float radiusX, float radiusZ) {
        List<Entity> entities = new ArrayList<>();
        for (Entity e : getEntities()) {
            if (e.isInside(position.x - radiusX, position.z - radiusZ, position.x + radiusX, position.z + radiusZ)) {
                entities.add(e);
            }
        }

        return entities;
    }

    public List<Light> getLightsInSquare(float posX0, float posZ0, float posX1, float posZ1) {
        List<Light> entities = new ArrayList<>();
        for (Light l : getLights()) {
            if (l.isInside(posX0, posZ0, posX1, posZ1)) {
                entities.add(l);
            }
        }

        return entities;
    }

    public List<Light> getLightsInSquare(Vector3f position, float radiusX, float radiusZ) {
        List<Light> entities = new ArrayList<>();
        for (Light l : getLights()) {
            if (l.isInside(position.x - radiusX, position.z - radiusZ, position.x + radiusX, position.z + radiusZ)) {
                entities.add(l);
            }
        }

        return entities;
    }

    public float getGravity() {
        return gravity;
    }
}
