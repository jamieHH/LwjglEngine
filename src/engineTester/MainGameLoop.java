package engineTester;

import engineTester.assets.Models;
import entities.*;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMasterRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import particles.ParticleEmitter;
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
//		for (int i = 0; i < 100000; i++) {
//			float x = random.nextFloat() * 800;
//			float z = random.nextFloat() * 800;
//			float y = terrain.getHeightOfTerrain(x, z);
//			world.addEntity(
//			        new Entity(Models.grass, random.nextFloat() + 1),
//                    x, y, z,
//                    0, randRotation(), 0
//            );
//		}
//		for (int i = 0; i < 2000; i++) {
//			float x = random.nextFloat() * 800;
//			float z = random.nextFloat() * 800;
//			float y = terrain.getHeightOfTerrain(x, z);
//            world.addEntity(
//                    new Entity(Models.rock, random.nextFloat() + 0.5f),
//                    x, y, z,
//                    0, randRotation(), 0
//            );
//		}
        for (int i = 0; i < 260; i++) { // 260 = 100fps| target: 300 better fps
            float x = random.nextFloat() * 100;
            float z = random.nextFloat() * 100;
            float y = terrain.getHeightOfTerrain(x, z);
            world.addEntity(
                    new Entity(Models.chair, 1),
                    x, y, z,
                    0, randRotation(), 0
            );
        }
//		for (int i = 0; i < 50; i++) {
//			float x = random.nextFloat() * 800;
//			float z = random.nextFloat() * 800;
//			float y = terrain.getHeightOfTerrain(x, z);
//            world.addLight(
//                    new Light(new Vector3f(0, 1, 0), 1f),
//                    x, y + 8, z
//            );
//			world.addEntity(
//			        new Entity(Models.lamp, 1),
//                    x, y, z,
//                    0, 0, 0
//            );
//		}
		//------

        world.addEntity(new Entity(Models.lightTest, 1), -20, 0, -20);
//        EnvLight ambient = new EnvLight(new Vector3f(world.getSkyR()+1, world.getSkyG()+1, world.getSkyB()+1), new Vector3f(1, 0.75f, -1));
////        world.addEnvLight(ambient);
        world.addEnvLight(new EnvLight(new Vector3f(0f, 0f, 1f), new Vector3f(-1, 0.0f, 1)));
        world.addEnvLight(new EnvLight(new Vector3f(1f, 0f, 0f), new Vector3f(1, 0.0f, -1)));

//        Camera camera = new Camera();
//        camera.setRotation(new Vector3f(0f, 135f, 0f));
//        camera.setPosition(new Vector3f(0f, 20f, 0f));
//        camera.setWorld(world);
        Player player = new Player(Models.chair, 1);
        player.setRotation(new Vector3f(0, 45, 0));
        world.addEntity(player, 0, 0, 0);
        OrbitalCamera camera = new OrbitalCamera(player);

        GuiRenderer.init();
        List<GuiTexture> guis = new ArrayList<>();
        guis.add(new GuiTexture(Loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f)));

        WorldMasterRenderer.init(world);

        TextMasterRenderer.init();
        FontType font = new FontType(Loader.loadFontTexture("font/arial"), new File("res/font/arial.fnt"));
        GUIText text = new GUIText("Demo", 1f, font, new Vector2f(0f, 0f), 1f, false);
        text.setColor(1, 1, 0);

        MousePicker picker = new MousePicker(camera, WorldMasterRenderer.getProjectionMatrix(), terrain);
        ParticleTexture particleTexture = new ParticleTexture(Loader.loadTexture("star0"));
        ParticleEmitter emitter = new ParticleEmitter(particleTexture, 1000, 2, 1);
        world.addParticleEmitter(emitter, 200, 100, 200);

		//--RUN
        int lampWait = 0;

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
                world.tick();
                player.tick();
                camera.tick();

                emitter.tick();
                if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
                    emitter.emitParticles();
                }
                picker.update();
                Vector3f tp = picker.getCurrentTerrainPoint();
                if (tp != null) {
                    if (lampWait > 0) {
                        lampWait--;
                    } else {
                        if (Mouse.isButtonDown(0)) {
                            lampWait = 15;
                            world.addLight(
                                    new Light(new Vector3f(0, 1, 0), 1f),
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
                //--EndTick
                updates++;
                delta--;
            }
            //--Render
            WorldMasterRenderer.render(camera);
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
