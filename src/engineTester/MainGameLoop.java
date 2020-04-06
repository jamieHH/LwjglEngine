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
		
//		float[] vertices = {			
//				-0.5f,0.5f,0,	
//				-0.5f,-0.5f,0,	
//				0.5f,-0.5f,0,	
//				0.5f,0.5f,0,		
//				
//				-0.5f,0.5f,1,	
//				-0.5f,-0.5f,1,	
//				0.5f,-0.5f,1,	
//				0.5f,0.5f,1,
//				
//				0.5f,0.5f,0,	
//				0.5f,-0.5f,0,	
//				0.5f,-0.5f,1,	
//				0.5f,0.5f,1,
//				
//				-0.5f,0.5f,0,	
//				-0.5f,-0.5f,0,	
//				-0.5f,-0.5f,1,	
//				-0.5f,0.5f,1,
//				
//				-0.5f,0.5f,1,
//				-0.5f,0.5f,0,
//				0.5f,0.5f,0,
//				0.5f,0.5f,1,
//				
//				-0.5f,-0.5f,1,
//				-0.5f,-0.5f,0,
//				0.5f,-0.5f,0,
//				0.5f,-0.5f,1
//
//		};
//		
//		float[] textureCoords = {
//				
//				0,0,
//				0,1,
//				1,1,
//				1,0,			
//				0,0,
//				0,1,
//				1,1,
//				1,0,			
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0,
//				0,0,
//				0,1,
//				1,1,
//				1,0
//
//				
//		};
//		
//		int[] indices = {
//				0,1,3,	
//				3,1,2,	
//				4,5,7,
//				7,5,6,
//				8,9,11,
//				11,9,10,
//				12,13,15,
//				15,13,14,	
//				16,17,19,
//				19,17,18,
//				20,21,23,
//				23,21,22
//
//		};
//		RawModel model = loader.loadToVAO(vertices,textureCoords,indices);
//		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("image")));

		RawModel model = OBJLoader.loadObjModel("chair0", loader);
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("wood")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(1000);
		texture.setReflectivity(1);


		Light light = new Light(new Vector3f(-5, 0, -5), new Vector3f(1, 1, 1));
        Entity entity = new Entity(staticModel, new Vector3f(0,0,-10),0,0,0,1);
        Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));

		Camera camera = new Camera();

		List<Entity> entities = new ArrayList<>();
		Random random = new Random();
		for (int i = 0; i < 20000; i++) {
			float x = random.nextFloat() * 100 - 50;
			float y = random.nextFloat() * 100 - 50;
			float z = random.nextFloat() * 100 - 50;
			entities.add(new Entity(staticModel, new Vector3f(x, y, z), random.nextFloat() * 180f,
					random.nextFloat() * 180f, 0f, 1f));
		}

		MasterRenderer renderer = new MasterRenderer();
		while(!Display.isCloseRequested()){
			light.move();
			camera.move();
//
//			for (Entity entity : entities) {
//				random.nextFloat();
//				entity.increaseRotation(0.1f, 0.1f, 0.1f);
//				renderer.processEntity(entity);
//			}

//			renderer.processTerrain(terrain);
			renderer.processEntity(entity);

			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
