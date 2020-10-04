package engine.postProcessing;

import engine.bloom.BrightFilter;
import engine.bloom.CombineFilter;
import engine.gaussianBlur.HorizontalBlur;
import engine.gaussianBlur.VerticalBlur;
import engine.loaders.Loader;
import engine.models.RawModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur2;
	private static VerticalBlur vBlur2;
	private static BrightFilter brightFilter;
	private static CombineFilter combineFilter;

	public static void init() {
		quad = Loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		vBlur = new VerticalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		hBlur2 = new HorizontalBlur(Display.getWidth() / 2, Display.getHeight() / 4);
		vBlur2 = new VerticalBlur(Display.getWidth() / 2, Display.getHeight() / 4);
		brightFilter = new BrightFilter(Display.getWidth() / 2, Display.getHeight() / 2);
		combineFilter = new CombineFilter();
	}
	
	public static void doPostProcessing(int colorTexture) {
		start();
		brightFilter.render(colorTexture, 0.8f);
		hBlur2.render(brightFilter.getOutputTexture());
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur.render(vBlur2.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());
		combineFilter.render(colorTexture, vBlur.getOutputTexture(), 1f);
		contrastChanger.render(combineFilter.getOutputTexture(), 0.2f);
		end();
	}
	
	public static void cleanUp() {
		contrastChanger.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		brightFilter.cleanUp();
		combineFilter.cleanUp();
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	public static RawModel getQuad() {
		return quad;
	} // TODO: move to a generic place one deferred shading is complete
}
