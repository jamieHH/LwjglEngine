package engine.bloom;

import engine.postProcessing.ImageRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class CombineFilter {
	
	private ImageRenderer renderer;
	private CombineShader shader;
	
	public CombineFilter(){
		shader = new CombineShader();
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
		renderer = new ImageRenderer(Display.getWidth(), Display.getHeight());
	}
	
	public void render(int colorTexture, int highlightTexture, float blendFactor) {
		shader.start();
		shader.loadBlendFactor(blendFactor);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		renderer.renderQuad();
		shader.stop();
	}

	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}

}
