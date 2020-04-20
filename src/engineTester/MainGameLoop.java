package engineTester;

import engineTester.assets.Models;
import entities.*;
import org.lwjgl.input.Mouse;
import renderEngine.GuiRenderer;
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
//		for (int i = 0; i < 2; i++) {
//			float x = random.nextFloat() * 200;
//			float z = random.nextFloat() * 200;
//			float y = terrain.getHeightOfTerrain(x, z);
//            world.addLight(
//                    new Light(new Vector3f(2, 0, 2), new Vector3f(1, 0.01f, 0.002f)),
//                    x, y + 8, z
//            );
//			world.addEntity(
//			        new Entity(Models.lamp, 1),
//                    x, y, z,
//                    0, 0, 0
//            );
//		}
		//------


        Light ambient = new Light(new Vector3f(0.3f, 0.3f, 0.3f));
        world.addLight(ambient, 400, 1000, 400);
        Light torch = new Light(new Vector3f(1, 1, 1), new Vector3f(1, 0.01f, 0.002f));
        world.addLight(torch, 0, 0, 0);


		Player player = new Player(Models.chair, 1);
		OrbitalCamera camera = new OrbitalCamera(player);
		world.addEntity(player, 0, 0, 0);
		world.addEntity(new Entity(Models.lightTest, 1), -20, 0, -20);

        List<GuiTexture> guis = new ArrayList<>();
        guis.add(new GuiTexture(Loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f)));

		GuiRenderer guiRenderer = new GuiRenderer();
		MasterRenderer worldRenderer = new MasterRenderer(world);

        MousePicker picker = new MousePicker(camera, worldRenderer.getProjectionMatrix(), terrain);
        Entity lampPole = new Entity(Models.lamp, 1);
        Light lampLight = new Light(new Vector3f(2, 0, 2), new Vector3f(1, 0.01f, 0.002f));
        world.addEntity(lampPole, 0, 0, 0);
        world.addLight(lampLight, 0, 0, 0);

		//--RUN
        int lampWait = 0;

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        while(!Display.isCloseRequested()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                //--Tick (camera and torch)
                player.tick();
                torch.setPosition(new Vector3f(player.getPosition().x, player.getPosition().y + 4, player.getPosition().z));
                camera.tick();

                picker.update();
                Vector3f tp = picker.getCurrentTerrainPoint();
                if (tp != null) {
                    lampPole.setPosition(new Vector3f(tp.getX(), tp.getY(), tp.getZ()));
                    lampLight.setPosition(new Vector3f(tp.getX(), tp.getY() + 8, tp.getZ()));
                    if (lampWait > 0) {
                        lampWait--;
                    } else {
                        if (Mouse.isButtonDown(0)) {
                            lampWait = 15;
                            world.addLight(
                                    new Light(new Vector3f(2, 0, 2), new Vector3f(1, 0.01f, 0.002f)),
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
            worldRenderer.processWorld();
            worldRenderer.render(camera);
            guiRenderer.render(guis);
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

		guiRenderer.cleanUp();
		worldRenderer.cleanUp();
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
