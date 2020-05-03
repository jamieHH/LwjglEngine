package normalMappingRenderer;

import java.util.List;
import java.util.Map;

import entities.EnvLight;
import entities.Point;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.WorldMasterRenderer;
import textures.ModelTexture;
import toolbox.Maths;

public class NormalMappingRenderer {

	private static NormalMappingShader shader = new NormalMappingShader();

	public static void init(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public static void render(Map<TexturedModel, List<Entity>> entities, List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
		prepare(lights, envLights, skyColor, camera);
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
		finish();
	}

	private static void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		// bind vertexes
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		ModelTexture texture = model.getTexture();
		if (texture.isHasTransparency()) {
			WorldMasterRenderer.disableCulling();
		}
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		// bind textures
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMapID());
	}

	private static void unbindTexturedModel() {
		WorldMasterRenderer.enableCulling();
		// unbind vertexes
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}

	private static void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}

	private static void prepare(List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		shader.start();
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
		shader.loadSkyColor(skyColor);
	}

	private static void finish() {
		shader.stop();
	}

	public static void cleanUp(){
		shader.cleanUp();
	}
}
