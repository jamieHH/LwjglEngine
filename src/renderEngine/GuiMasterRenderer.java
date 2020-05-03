package renderEngine;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import fontRendering.FontRenderer;
import gui.GuiTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiMasterRenderer {

    private static Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static List<GuiTexture> textures = new ArrayList<>();

    public static void init() {
        GuiRenderer.init();
        FontRenderer.init();
    }

    public static void render() {
        GuiRenderer.render(textures);
        FontRenderer.render(texts);
    }

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    public static void loadTexture(GuiTexture texture) {
        textures.add(texture);
    }

    public static void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            texts.remove(text.getFont());
        }
    }

    public static void clearText() {
        texts.clear();
    }

    public static void removeTexture(GuiTexture texture) {
        textures.remove(texture);
    }

    public static void cleanUp() {
        GuiRenderer.cleanUp();
        FontRenderer.cleanUp();
    }
}
