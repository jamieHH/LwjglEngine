package engine.terrains;

import engine.entities.EnvLight;
import engine.entities.Light;
import engine.entities.Point;
import engine.models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engine.utils.Maths;

import java.util.List;

public class TerrainRenderer {

	private static TerrainShader shader = new TerrainShader();

	public static void init(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public static void render(List<Terrain> terrains, List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadSkyColor(skyColor);
		shader.loadEnvLights(envLights);
		shader.loadLights(lights);
		for (Terrain terrain : terrains) {
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			// draw call
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		} // TODO: a texture is not being unbound somewhere
		shader.stop();
	}

	private static void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		// bind vertexes
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
		shader.loadShineVariables(1, 0);
	}

	private static void bindTextures(Terrain terrain) {
		// bind engine.textures
		TerrainTexturePack texturesPack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturesPack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturesPack.getrTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturesPack.getgTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturesPack.getbTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}

	private static void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), new Vector3f(0, 0, 0), 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

	private static void unbindTexturedModel() {
		// unbind vertexes
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public static void cleanUp() {
		shader.cleanUp();
	}
}
