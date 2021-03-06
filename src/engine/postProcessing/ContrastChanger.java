package engine.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger {

    private ImageRenderer renderer;
    private ContrastShader shader;

    public ContrastChanger() {
        renderer = new ImageRenderer();
        shader = new ContrastShader();
    }

    public void render(int texture, float contrast) {
        shader.start();
        shader.loadContrast(contrast);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp() {
        renderer.cleanUp();
        shader.cleanUp();
    }
}
