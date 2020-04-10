package engineTester;

import entities.*;
import models.RawModel;
import models.TexturedModel;

import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;

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
        TexturedModel texturedChairModel = new TexturedModel(
                chairModel,
                new ModelTexture(loader.loadTexture("wood"))
        );
        Entity chair0 = new Entity(texturedChairModel, new Vector3f(0,0,-10),0,0,0,1);


//		TexturedModel chairModel = new TexturedModel(
//				OBJLoader.loadObjModel("chair0", loader) ,
//				new ModelTexture(loader.loadTexture("wood"))
//		);
//		Entity chair0 = new Entity(chairModel, new Vector3f(0,0,-10),0,0,0,1);


        TexturedModel grassModel = new TexturedModel(
        		OBJLoader.loadObjModel("grass0", loader),
				new ModelTexture(loader.loadTexture("grass"))
		);
        grassModel.getTexture().setHasTransparency(true);
        grassModel.getTexture().setUseFakeLighting(true);
        Entity grass0 = new Entity(grassModel, new Vector3f(0,0,-10),0,0,0,1);


        //*** START TERRAIN MAPPING ***

		TerrainTexture bgTexture = new TerrainTexture(loader.loadTexture("grassTex"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirtTex"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowerTex"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("pathTex"));
		TerrainTexturePack texturePack = new TerrainTexturePack(bgTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap);

		//*** EDN TERRAIN MAPPING ***


		Light light = new Light(new Vector3f(-5, 0, -5), new Vector3f(1, 1, 1));
		Player player = new Player(texturedChairModel, new Vector3f(0, 0, -10), 0, 0, 0, 1);
		OrbitalCamera camera = new OrbitalCamera(player);

		List<Entity> entities = new ArrayList<>();
//		entities.add(chair0);
		entities.add(grass0);


		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()) {
//			light.move();
			player.move();
			camera.move();

			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for (Entity entity : entities) {
//				entity.increaseRotation(0.1f, 0.1f, 0.1f);
				renderer.processEntity(entity);
			}

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
