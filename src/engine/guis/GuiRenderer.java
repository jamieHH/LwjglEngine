package engine.guis;

import engine.models.RawModel;
import engine.loaders.Loader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import engine.utils.Maths;

import java.util.List;

public class GuiRenderer {

    private static final RawModel quad = Loader.loadToVAO(new float[] {-1, 1, -1, -1, 1, 1, 1, -1}, 2);
    private static GuiShader shader = new GuiShader();

    public static void init() {

    }

    public static void render(List<GuiTexture> guis) {
        prepare();
        for (GuiTexture gui : guis) {
            // bind engine.textures
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTextureId());
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
            shader.loadTransformation(matrix);
            // draw call
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        finish();
    }

    private static void prepare() {
        shader.start();
        // bind vertexes
        GL30.glBindVertexArray(quad.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void finish() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        // unbind vertexes
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public static void cleanUp() {
        shader.cleanUp();
    }
}
