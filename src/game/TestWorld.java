package game;

import engine.entities.Entity;
import engine.entities.EnvLight;
import engine.entities.Light;
import engine.skybox.Skybox;
import org.lwjgl.util.vector.Vector3f;
import engine.entities.ParticleEmitter;
import engine.particles.ParticleTexture;
import engine.loaders.Loader;
import engine.terrains.Terrain;
import engine.terrains.TerrainTexture;
import engine.terrains.TerrainTexturePack;
import engine.world.World;

import java.util.Random;

public class TestWorld extends World {

    private String[] primaryTextures = {
            "skybox/day/right",
            "skybox/day/left",
            "skybox/day/top",
            "skybox/day/bottom",
            "skybox/day/back",
            "skybox/day/front",
    };
    private String[] secondaryTextures = {
            "skybox/night/right",
            "skybox/night/left",
            "skybox/night/top",
            "skybox/night/bottom",
            "skybox/night/back",
            "skybox/night/front",
    };

    public TestWorld() {

        super.setSkybox(new Skybox(primaryTextures, secondaryTextures));
        super.setSkyColor(new Vector3f(0.5f, 0.6f, 0.7f));

        //-------------- EnvLights
        this.addEnvLight(new EnvLight(new Vector3f(this.getSkyR()+1, this.getSkyG()+1, this.getSkyB()+1), new Vector3f(1, 1, -1)));
        //--------------

        //-------------- Terrain
        TerrainTexture backgroundTexture = new TerrainTexture(Loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(Loader.loadTexture("mud"));
        TerrainTexture gTexture = new TerrainTexture(Loader.loadTexture("flowers"));
        TerrainTexture bTexture = new TerrainTexture(Loader.loadTexture("path"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendmap = new TerrainTexture(Loader.loadTexture("blendMap"));
        Terrain terrain = new Terrain(0, 0, texturePack, blendmap, "terrainHeightmap");
        this.setTerrain(terrain);
        //--------------

        //---- Random Scene objects
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            this.addEntity(
                    new Entity(Models.grass, random.nextInt(2) + 2),
                    x, y, z,
                    0, randRotation(), 0
            );
        }
        for (int i = 0; i < 200; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            this.addEntity(
                    new Entity(Models.rock, random.nextFloat() + 0.5f),
                    x, y, z,
                    0, randRotation(), 0
            );
        }
//        for (int i = 0; i < 50; i++) {
//            float x = random.nextFloat() * 800;
//            float z = random.nextFloat() * 800;
//            float y = terrain.getHeightOfTerrain(x, z);
//            this.addLight(
//                    new Light(new Vector3f(0, 1, 0), 1f),
//                    x, y + 8, z
//            );
//            this.addEntity(
//                    new Entity(Models.lamp, 1),
//                    x, y, z,
//                    0, 0, 0
//            );
//        }
//        for (int i = 0; i < 200; i++) {
//            float x = random.nextFloat() * 100;
//            float z = random.nextFloat() * 100;
//            float y = terrain.getHeightOfTerrain(x, z);
//            Entity e = new Entity(Models.chair, 1);
//            e.setSolid(true);
//            this.addEntity(e, x, y, z, 0, randRotation(), 0);
//        }
//        this.addEntity(new Entity(Models.lightTest, 1), -20, 0, -20);
        //------

        for (int x = -2; x < 3; x++) {
            for (int y = -2; y < 3; y++) {
                for (int z = -2; z < 3; z++) {
                    this.addEntity(new Entity(Models.sphere, 1), x*20, y*20, z*20);
                }
            }
        }

    }

    private static float randRotation() {
        return new Random().nextFloat() * 360;
    }
}
