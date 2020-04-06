package engineTester;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		StaticShader shader = new StaticShader();
		Renderer renderer = new Renderer(shader);
		
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
//		
//		RawModel model = loader.loadToVAO(vertices,textureCoords,indices);
//		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("image")));
		
		RawModel model = OBJLoader.loadObjModel("chair0", loader);
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("wood")));
		ModelTexture texture = staticModel.getTexture();
		texture.setShineDamper(1000);
		texture.setReflectivity(1);
		
		
		Light light = new Light(new Vector3f(0, 0, -5), new Vector3f(1, 1, 1));
		Entity entity = new Entity(staticModel, new Vector3f(0,0,-10),0,0,0,1);
		
		Camera camera = new Camera();
		
		while(!Display.isCloseRequested()){
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				light.position.z-=0.05f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				light.position.x+=0.05f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				light.position.x-=0.05f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				light.position.z+=0.05f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
				light.position.y+=0.05f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
				light.position.y-=0.05f;
			}
			
			entity.increaseRotation(0, 0.0f, 0);
//			camera.move();
			
			renderer.prepare();
			shader.start();
			shader.loadLight(light);
			shader.loadViewMatrix(camera);
			renderer.render(entity,shader);
			shader.stop();
			DisplayManager.updateDisplay();
		}

		shader.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
