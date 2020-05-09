package engine.postProcessing;

import engine.loaders.Loader;
import engine.models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;

	public static void init() {
		quad = Loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
	}
	
	public static void doPostProcessing(int colorTexture) {
		start();
		contrastChanger.render(colorTexture);
		end();
	}
	
	public static void cleanUp() {
		contrastChanger.cleanUp();
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
}
