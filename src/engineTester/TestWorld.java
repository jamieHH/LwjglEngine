package engineTester;

import engineTester.assets.Models;
import entities.Entity;
import entities.EnvLight;
import org.lwjgl.util.vector.Vector3f;
import particles.ParticleEmitter;
import particles.ParticleTexture;
import renderEngine.Loader;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import world.World;

import java.util.Random;

public class TestWorld extends World {

    public TestWorld() {

        //-------------- EnvLights
//        this.addEnvLight(new EnvLight(new Vector3f(this.getSkyR()+1, this.getSkyG()+1, this.getSkyB()+1), new Vector3f(1, 0.75f, -1)));
        this.addEnvLight(new EnvLight(new Vector3f(0f, 0f, 1f), new Vector3f(-1, 0.0f, 1)));
        this.addEnvLight(new EnvLight(new Vector3f(1f, 0f, 0f), new Vector3f(1, 0.0f, -1)));
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
//        for (int i = 0; i < 100000; i++) {
//            float x = random.nextFloat() * 800;
//            float z = random.nextFloat() * 800;
//            float y = terrain.getHeightOfTerrain(x, z);
//            this.addEntity(
//                    new Entity(Models.grass, random.nextFloat() + 1),
//                    x, y, z,
//                    0, randRotation(), 0
//            );
//        }
//        for (int i = 0; i < 2000; i++) {
//            float x = random.nextFloat() * 800;
//            float z = random.nextFloat() * 800;
//            float y = terrain.getHeightOfTerrain(x, z);
//            this.addEntity(
//                    new Entity(Models.rock, random.nextFloat() + 0.5f),
//                    x, y, z,
//                    0, randRotation(), 0
//            );
//        }
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
        for (int i = 0; i < 260; i++) { // 260 = 100fps| target: 300 better fps
            float x = random.nextFloat() * 100;
            float z = random.nextFloat() * 100;
            float y = terrain.getHeightOfTerrain(x, z);
            this.addEntity(
                    new Entity(Models.chair, 1),
                    x, y, z,
                    0, randRotation(), 0
            );
        }
        this.addEntity(new Entity(Models.lightTest, 1), -20, 0, -20);

        ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture("star0"));
        ParticleEmitter emitter = new ParticleEmitter(particleTexture, 1000, 2, 1);
        this.addParticleEmitter(emitter, 200, 100, 200);
        //------


    }

    private static float randRotation() {
        return new Random().nextFloat() * 360;
    }
}
