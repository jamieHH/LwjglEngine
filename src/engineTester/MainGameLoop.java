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

        ModelData chairData = OBJFileLoader.loadOBJ("chair0");
        RawModel chairModel = loader.loadToVAO(
                chairData.getVertices(),
                chairData.getTextureCoords(),
                chairData.getNormals(),
                chairData.getIndices()
        );
        TexturedModel texturedChairModel = new TexturedModel(chairModel, new ModelTexture(loader.loadTexture("wood")));

//        TexturedModel grassModel = new TexturedModel(
//        		OBJLoader.loadObjModel("grass0", loader),
//				new ModelTexture(loader.loadTexture("grass"))
//		);
//        grassModel.getTexture().setHasTransparency(true);
//        grassModel.getTexture().setUseFakeLighting(true);
//        Entity grass0 = new Entity(grassModel, new Vector3f(0,0,-10),0,0,0,1);

		//--------------
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendmap = new TerrainTexture(loader.loadTexture("blendMap"));
		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendmap, "terrainHeightmap");
		//--------------

		Light light = new Light(new Vector3f(0, 10, 0), new Vector3f(1, 1, 1));
		Player player = new Player(texturedChairModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		OrbitalCamera camera = new OrbitalCamera(player);

		List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 2000; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(texturedChairModel, new Vector3f(x, y, z),0,0,0,1));
        }

		List<GuiTexture> guis = new ArrayList<>();
		guis.add(new GuiTexture(loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f)));
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()) {
//			light.move();
			player.move(terrain);
			camera.move();

			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (Entity entity : entities) {
				renderer.processEntity(entity);
			}

			renderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
