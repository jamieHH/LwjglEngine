package renderEngine;

import entities.EnvLight;
import entities.Light;
import entities.Point;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector3f;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

import entities.Entity;

import java.util.List;
import java.util.Map;

public class EntityRenderer {

	private StaticShader shader;
	
	public EntityRenderer(Matrix4f projectionMatrix) {
		this.shader = new StaticShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entities, List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadSkyColor(skyColor);
		shader.loadEnvLights(envLights);
		shader.loadLights(lights);
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				// draw call
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
	}

	private void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		// bind vertexes
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		if (texture.isHasTransparency()) {
			WorldMasterRenderer.disableCulling();
		}
		shader.loadFakeLightingVariariable(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		// bind textures
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	private void unbindTexturedModel() {
		WorldMasterRenderer.enableCulling();
		// unbind vertexes
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public void cleanUp(){
		shader.cleanUp();
	}
}
