package engineTester;

import engineTester.assets.Models;
import entities.*;
import renderEngine.GuiRenderer;
import gui.GuiTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
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
		//--------------

        List<GuiTexture> guis = new ArrayList<>();
        guis.add(new GuiTexture(Loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f)));

        Random random = new Random();
		for (int i = 0; i < 100000; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * 800;
			float y = terrain.getHeightOfTerrain(x, z);
//			entities.add(new Entity(Models.grass, new Vector3f(x, y, z),0, randRotation(),0,random.nextFloat() + 1));
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
//			entities.add(new Entity(Models.rock, new Vector3f(x, y, z), randRotation(), randRotation(), randRotation(),random.nextFloat() + 0.5f));
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

        world.addLight(
                new Light(new Vector3f(0.3f, 0.4f, 0.4f), new Vector3f(1, 0, 0)),
                400, 1000, 400
        );

        Light torch = new Light(new Vector3f(2, 2, 1), new Vector3f(1, 0.01f, 0.002f));
        world.addLight(torch, 0, 0, 0);
		for (int i = 0; i < 2; i++) {
			float x = random.nextFloat() * 200;
			float z = random.nextFloat() * 200;
			float y = terrain.getHeightOfTerrain(x, z);
            world.addLight(
                    new Light(new Vector3f(2, 0, 2), new Vector3f(1, 0.01f, 0.002f)),
                    x, y + 8, z
            );
			world.addEntity(
			        new Entity(Models.lamp, 1),
                    x, y, z,
                    0, 0, 0
            );
		}

		Player player = new Player(Models.chair, 1);
		OrbitalCamera camera = new OrbitalCamera(player);
		world.setPlayer(player, 0, 0, 0);

		GuiRenderer guiRenderer = new GuiRenderer();
		MasterRenderer renderer = new MasterRenderer(world);
		while(!Display.isCloseRequested()) {
			player.move(terrain);
			Vector3f torchPos = new Vector3f(player.getPosition().x,
                    player.getPosition().y + 4,
                    player.getPosition().z);
			torch.setPosition(torchPos);
			camera.move();

			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (Entity entity : world.getEntities()) {
				renderer.processEntity(entity);
			}

			renderer.render(world.getLights(), camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	private static float randRotation() {
	    return new Random().nextFloat() * 360;
    }
}
