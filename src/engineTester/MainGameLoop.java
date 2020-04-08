package engineTester;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();


		RawModel model = OBJLoader.loadObjModel("chair0", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("wood")));
		Entity chair0 = new Entity(staticModel, new Vector3f(0,0,-10),0,0,0,1);

		Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));
		Light light = new Light(new Vector3f(-5, 0, -5), new Vector3f(1, 1, 1));
		Camera camera = new Camera();

		List<Entity> entities = new ArrayList<>();
		entities.add(chair0);


		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()){
//			light.move();
			camera.move();

			renderer.processTerrain(terrain);
			for (Entity entity : entities) {
				entity.increaseRotation(0.1f, 0.1f, 0.1f);
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
