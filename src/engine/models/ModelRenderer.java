package engine.models;

import engine.entities.*;

import engine.loaders.Loader;
import engine.renderEngine.WorldMasterRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector3f;
import engine.utils.Maths;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

public class ModelRenderer {

	private static final int MAX_INSTANCES = 100000;
	private static final int INSTANCE_DATA_LENGTH = 32;
	private static FloatBuffer buffer;

	private static int vbo;
	private static int pointer = 0;

	private static ModelShader shader = new ModelShader();
	
	public static void init(Matrix4f projectionMatrix) {
		vbo = Loader.createFloatVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public static void render(Map<TexturedModel, List<Entity>> entities, List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
		prepare(lights, envLights, skyColor, camera);
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            pointer = 0;
			float[] vboData = new float[batch.size() * INSTANCE_DATA_LENGTH];
            buffer = BufferUtils.createFloatBuffer(batch.size() * INSTANCE_DATA_LENGTH);
			for (Entity entity : batch) {
				prepareInstance(entity, camera, vboData);
			}
			Loader.updateVbo(vbo, vboData, buffer);
			GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0, batch.size());
			unbindTexturedModel();
		}
		finish();
	}

	private static void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();

		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 0);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 4);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 8);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 6, 4, INSTANCE_DATA_LENGTH, 12);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 7, 4, INSTANCE_DATA_LENGTH, 16);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 8, 4, INSTANCE_DATA_LENGTH, 20);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 9, 4, INSTANCE_DATA_LENGTH, 24);
		Loader.addInstancedAttribute(rawModel.getVaoID(), vbo, 10, 4, INSTANCE_DATA_LENGTH, 28);

		// bind vertexes
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		GL20.glEnableVertexAttribArray(7);
		GL20.glEnableVertexAttribArray(8);
		GL20.glEnableVertexAttribArray(9);
		GL20.glEnableVertexAttribArray(10);
		ModelTexture texture = model.getTexture();
		if (texture.isHasTransparency()) {
			WorldMasterRenderer.disableCulling();
		}
		shader.loadFakeLightingVariariable(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		// bind engine.textures
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private static void prepareInstance(Entity entity, Point camera, float[] vboData) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
				entity.getRotation(), entity.getScale());
		storeMatrixData(transformationMatrix, vboData);
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		storeMatrixData(viewMatrix, vboData);
	}

	private static void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00;
		vboData[pointer++] = matrix.m01;
		vboData[pointer++] = matrix.m02;
		vboData[pointer++] = matrix.m03;
		vboData[pointer++] = matrix.m10;
		vboData[pointer++] = matrix.m11;
		vboData[pointer++] = matrix.m12;
		vboData[pointer++] = matrix.m13;
		vboData[pointer++] = matrix.m20;
		vboData[pointer++] = matrix.m21;
		vboData[pointer++] = matrix.m22;
		vboData[pointer++] = matrix.m23;
		vboData[pointer++] = matrix.m30;
		vboData[pointer++] = matrix.m31;
		vboData[pointer++] = matrix.m32;
		vboData[pointer++] = matrix.m33;
	}

	private static void prepare(List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
		shader.start();
		shader.loadSkyColor(skyColor);
		shader.loadEnvLights(envLights);
		shader.loadLights(lights);
	}

	private static void finish() {
		shader.stop();
	}

	private static void unbindTexturedModel() {
		WorldMasterRenderer.enableCulling();
		// unbind vertexes
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL20.glDisableVertexAttribArray(7);
		GL20.glDisableVertexAttribArray(8);
		GL20.glDisableVertexAttribArray(9);
		GL20.glDisableVertexAttribArray(10);
		GL30.glBindVertexArray(0);
	}

	public static void cleanUp(){
		shader.cleanUp();
	}
}
