package engine.guis.font;

import engine.guis.fontLoader.FontType;
import engine.guis.fontLoader.GuiText;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class FontRenderer {

	private static FontShader shader = new FontShader();

	public static void init() {

	}

	public static void render(Map<FontType, List<GuiText>> texts) {
		prepare();
		for (FontType font : texts.keySet()) {
			// bind engine.textures
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for (GuiText text : texts.get(font)) {
				shader.loadColor(text.getColor());
				shader.loadTranslation(text.getPosition());
				// bind vertexes
				GL30.glBindVertexArray(text.getMesh());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				// draw call
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
				// unbind vertexes
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL30.glBindVertexArray(0);
			}
		}
		finish();
	}

	private static void prepare() {
		shader.start();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void finish() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		shader.stop();
	}

	public static void cleanUp(){
		shader.cleanUp();
	}
}
