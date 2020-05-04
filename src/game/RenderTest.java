package game;

import engine.entities.Entity;
import engine.entities.EnvLight;
import engine.entities.Light;
import engine.entities.ParticleEmitter;
import engine.loaders.Loader;
import engine.particles.ParticleTexture;
import engine.terrains.Terrain;
import engine.terrains.TerrainTexture;
import engine.terrains.TerrainTexturePack;
import engine.world.World;
import org.lwjgl.util.vector.Vector3f;

public class RenderTest extends World {

    public RenderTest() {
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

        for (int x = 0; x < 800; x++) {
            for (int z = 0; z < 800; z++) {
                if (x % 10 == 0 && z % 10 == 0) {
                    this.addEntity(
                            new Entity(Models.rock, 1),
                            x, this.getTerrain().getHeightOfTerrain(x, z), z,
                            0, 0, 0
                    );
                    this.addEntity(
                            new Entity(Models.grass, 2),
                            x+4, this.getTerrain().getHeightOfTerrain(x+4, z+4), z+4,
                            0, 0, 0
                    );
                    this.addEntity(new Entity(Models.chair, 1),
                            x+8, this.getTerrain().getHeightOfTerrain(x+8, z+8), z+8,
                            0, 0, 0);
                }
                if (x % 40 == 0 && z % 40 == 0) {
                    this.addLight(
                            new Light(new Vector3f(0, 0.25f, 0), 1f),
                            x, this.getTerrain().getHeightOfTerrain(x, z) + 8, z
                    );
                    this.addEntity(
                            new Entity(Models.lamp, 1),
                            x, this.getTerrain().getHeightOfTerrain(x, z), z,
                            0, 0, 0
                    );
                }
            }
        }

        ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture("star0"));
        ParticleEmitter emitter = new ParticleEmitter(particleTexture, 1000, 2, 1);
        this.addParticleEmitter(emitter, 400, 100, 400);
    }
}
