package world;

import entities.Entity;
import entities.Light;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.List;

public class World {

    private float skyR = 0.3f;
    private float skyG = 0.4f;
    private float skyB = 0.4f;

    private List<Entity> entities = new ArrayList<>();
    private List<Light> lights = new ArrayList<>();
    private Terrain terrain;

    public World() {

    }

    public float getSkyR() {
        return skyR;
    }

    public float getSkyG() {
        return skyG;
    }

    public float getSkyB() {
        return skyB;
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
    }

    public void addEntity(Entity entity, float posX, float posY, float posZ, float rotX, float rotY, float rotZ) {
        entity.setPosition(new Vector3f(posX, posY, posZ));
        entity.setRotation(new Vector3f(rotX, rotY, rotZ));
        entity.setWorld(this);
        entities.add(entity);
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
}
