package engineTester;

import engineTester.assets.Models;
import entities.*;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMasterRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import particles.Particle;
import particles.ParticleEmitter;
import particles.ParticleMasterRenderer;
import particles.ParticleTexture;
import gui.GuiTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import world.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
        World world = new World();

		//-------------- Terrain
		TerrainTexture backgroundTexture = new TerrainTexture(Loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTexture("flowers"));
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendmap = new TerrainTexture(Loader.loadTexture("blendMap"));
		Terrain terrain = new Terrain(0, 0, texturePack, blendmap, "terrainHeightmap");
		world.setTerrain(terrain);
		//--------------

		//---- Random Scene objects
        Random random = new Random();
		for (int i = 0; i < 100000; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * 800;
			float y = terrain.getHeightOfTerrain(x, z);
			world.addEntity(
			        new Entity(Models.grass, random.nextFloat() + 1),
                    x, y, z,
                    0, randRotation(), 0
            );
		}
		for (int i = 0; i < 2000; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * 800;
			float y = terrain.getHeightOfTerrain(x, z);
            world.addEntity(
                    new Entity(Models.rock, random.nextFloat() + 0.5f),
                    x, y, z,
                    0, randRotation(), 0
            );
		}
        for (int i = 0; i < 1000; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            world.addEntity(
                    new Entity(Models.chair, 1),
                    x, y, z,
                    0, randRotation(), 0
            );
        }
		for (int i = 0; i < 50; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * 800;
			float y = terrain.getHeightOfTerrain(x, z);
            world.addLight(
                    new Light(new Vector3f(1, 0, 1), new Vector3f(1, 0.01f, 0.002f)),
                    x, y + 8, z
            );
			world.addEntity(
			        new Entity(Models.lamp, 1),
                    x, y, z,
                    0, 0, 0
            );
		}
		//------


        EnvLight ambient = new EnvLight(new Vector3f(world.getSkyR() + 1, world.getSkyG() + 1, world.getSkyB() + 1), new Vector3f(1, 0.75f, -1));
        world.addEnvLight(ambient);
//        world.addEnvLight(new EnvLight(new Vector3f(0.0f, 0.0f, 1f), new Vector3f(-1, 0.2f, 1)));
//        world.addEnvLight(new EnvLight(new Vector3f(1f, 0.0f, 0.0f), new Vector3f(1, 0.2f, -1)));

        Light torch = new Light(new Vector3f(1, 1, 1), new Vector3f(1, 0.01f, 0.002f));
//        world.addLight(torch, 0, 0, 0);


//		Player player = new Player(Models.chair, 1);
//		world.addEntity(player, 0, 0, 0);
//        OrbitalCamera camera = new OrbitalCamera(player);
        Camera camera = new Camera();
        camera.setPosition(new Vector3f(0f, 20f, 0f));
        camera.setRotation(new Vector3f(0f, 135f, 0f));
        camera.setWorld(world);
        world.addEntity(new Entity(Models.lightTest, 1), -20, 0, -20);

        List<GuiTexture> guis = new ArrayList<>();
        guis.add(new GuiTexture(Loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f)));

		GuiRenderer.init();
		WorldMasterRenderer.init(world);

        TextMasterRenderer.init();
        FontType font = new FontType(Loader.loadFontTexture("font/arial"), new File("res/font/arial.fnt"));
        GUIText text = new GUIText("Demo", 1f, font, new Vector2f(0f, 0f), 1f, false);
        text.setColor(1, 0, 0);

        MousePicker picker = new MousePicker(camera, WorldMasterRenderer.getProjectionMatrix(), terrain);
        Entity lampPole = new Entity(Models.lamp, 1);
        Light lampLight = new Light(new Vector3f(0, 1, 0), new Vector3f(1, 0.01f, 0.002f));
        world.addEntity(lampPole, 0, 0, 0);
        world.addLight(lampLight, 0, 0, 0);

        Entity barrel = new Entity(Models.barrelModel, 1);
        world.addEntity(barrel, 10, 5, 10);

        ParticleMasterRenderer.init(WorldMasterRenderer.getProjectionMatrix());
        ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture("star0"));
        ParticleEmitter emitter = new ParticleEmitter(particleTexture, 1000, 2, 1);
        emitter.setPosition(new Vector3f(200, 100, 200));
        emitter.setWorld(world);

		//--RUN
        int lampWait = 0;
        int processWait = 0;

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        while (!Display.isCloseRequested()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                //--Tick (camera and torch)
//                player.tick();
//                torch.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 4, player.getPosition().z));
                camera.tick();

                if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
                    emitter.emitParticles();
                }
                ParticleMasterRenderer.tick(camera);

                picker.update();
                Vector3f tp = picker.getCurrentTerrainPoint();
                if (tp != null) {
                    lampPole.setPosition(new Vector3f(tp.getX(), tp.getY(), tp.getZ()));
                    lampLight.setPosition(new Vector3f(tp.getX(), tp.getY() + 8, tp.getZ()));
                    if (lampWait > 0) {
                        lampWait--;
                    } else {
                        if (Mouse.isButtonDown(1)) {
                            lampWait = 15;
                            world.addLight(
                                    new Light(lampLight.getColor(), lampLight.getAttenuation()),
                                    tp.getX(), tp.getY() + 8, tp.getZ()
                            );
                            world.addEntity(
                                    new Entity(Models.lamp, 1),
                                    tp.getX(), tp.getY(), tp.getZ(),
                                    0, 0, 0
                            );
                        }
                    }
                }

                barrel.setRotY(barrel.getRotY() + 0.2f);
                //--EndTick
                updates++;
                delta--;
            }
            //--Render
            if (processWait > 0) {
                processWait--;
            } else {
                processWait = 10;
                WorldMasterRenderer.clearProcessedWorld();
                WorldMasterRenderer.processWorld(camera);
            }
            WorldMasterRenderer.render(camera);
            ParticleMasterRenderer.renderParticles(camera);
            GuiRenderer.render(guis);
            TextMasterRenderer.render();
            DisplayManager.updateDisplay();
            //--EndRender
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames + " | " + "UPS: " + updates);
                frames = 0;
                updates = 0;
            }
		}
        //--ENDRUN

        ParticleMasterRenderer.cleanUp();
        TextMasterRenderer.cleanUp();
		GuiRenderer.cleanUp();
		WorldMasterRenderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	private void render() {

    }

    private void tick() {

    }

	private static float randRotation() {
	    return new Random().nextFloat() * 360;
    }
}
