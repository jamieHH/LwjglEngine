package engineTester;

import entities.*;
import renderEngine.GuiRenderer;
import gui.GuiTexture;
import models.RawModel;
import models.TexturedModel;

import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		//---------- Models
        ModelData chairData = OBJFileLoader.loadOBJ("chair0");
        RawModel chairModel = makeRawModel(chairData, loader);
        TexturedModel texturedChairModel = new TexturedModel(chairModel, new ModelTexture(loader.loadTexture("wood")));

		ModelData rockModelData = OBJFileLoader.loadOBJ("rock0HD");
		RawModel rockModel = makeRawModel(rockModelData, loader);
		ModelTexture rockTexture = new ModelTexture(loader.loadTexture("rock"));
		rockTexture.setReflectivity(0.5f);
		rockTexture.setShineDamper(1.0f);
		TexturedModel texturedRockModel = new TexturedModel(rockModel, rockTexture);

		ModelData grassModelData = OBJFileLoader.loadOBJ("grass0");
		RawModel grassModel = makeRawModel(grassModelData, loader);
		ModelTexture grassTexture = new ModelTexture(loader.loadTexture("grass"));
		grassTexture.setHasTransparency(true);
		grassTexture.setUseFakeLighting(true);
		TexturedModel texturedGrassModel = new TexturedModel(grassModel, grassTexture);

		ModelData lampModelData = OBJFileLoader.loadOBJ("lampPost0");
		RawModel lampModel = makeRawModel(lampModelData, loader);
		ModelTexture steelTexture = new ModelTexture(loader.loadTexture("steel"));
		steelTexture.setReflectivity(0.5f);
		steelTexture.setShineDamper(1.0f);
		TexturedModel texturedLampModel = new TexturedModel(lampModel, steelTexture);
        //----------

		//-------------- Terrain
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendmap = new TerrainTexture(loader.loadTexture("blendMap"));
		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendmap, "terrainHeightmap");
		//--------------

        List<GuiTexture> guis = new ArrayList<>();
        guis.add(new GuiTexture(loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f)));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
		for (int i = 0; i < 100000; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * 800;
			float y = terrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(texturedGrassModel, new Vector3f(x, y, z),0, randRotation(),0,random.nextFloat() + 1));
		}
		for (int i = 0; i < 2000; i++) {
			float x = random.nextFloat() * 800;
			float z = random.nextFloat() * 800;
			float y = terrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(texturedRockModel, new Vector3f(x, y, z), randRotation(), randRotation(), randRotation(),random.nextFloat() + 0.5f));
		}
        for (int i = 0; i < 1000; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(texturedChairModel, new Vector3f(x, y, z),0, randRotation(), 0, 1));
        }

        List<Light> lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(400, 1000, 400), new Vector3f(0.3f, 0.4f, 0.4f)));
		for (int i = 0; i < 2; i++) {
			float x = random.nextFloat() * 200;
			float z = random.nextFloat() * 200;
			float y = terrain.getHeightOfTerrain(x, z);
			lights.add(new Light(new Vector3f(x, y + 8, z),
					new Vector3f(2, 0, 2), new Vector3f(1, 0.01f, 0.002f)));
			entities.add(new Entity(texturedLampModel, new Vector3f(x, y, z), 0, 0, 0, 1));
		}

        Light torch = new Light(new Vector3f(20, 20, 20),
                new Vector3f(2, 2, 1), new Vector3f(1, 0.01f, 0.002f));
        lights.add(torch);

		Player player = new Player(texturedChairModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		OrbitalCamera camera = new OrbitalCamera(player);

		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()) {
			player.move(terrain);
			Vector3f torchPos = new Vector3f(player.getPosition().x,
                    player.getPosition().y + 4,
                    player.getPosition().z);
			torch.setPosition(torchPos);
			camera.move();

			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}

			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	private static RawModel makeRawModel(ModelData modelData, Loader loader) {
		return loader.loadToVAO(
				modelData.getVertices(),
				modelData.getTextureCoords(),
				modelData.getNormals(),
				modelData.getIndices()
		);
	}

	private static float randRotation() {
	    return new Random().nextFloat() * 360;
    }
}
