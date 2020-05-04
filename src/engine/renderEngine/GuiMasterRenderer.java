package engine.renderEngine;

import engine.guis.fontLoader.FontType;
import engine.guis.fontLoader.GuiText;
import engine.guis.fontLoader.TextMeshData;
import engine.guis.font.FontRenderer;
import engine.guis.GuiRenderer;
import engine.guis.GuiTexture;
import engine.loaders.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiMasterRenderer {

    private static Map<FontType, List<GuiText>> texts = new HashMap<>();
    private static List<GuiTexture> textures = new ArrayList<>();

    public static void init() {
        GuiRenderer.init();
        FontRenderer.init();
    }

    public static void render() {
        GuiRenderer.render(textures);
        FontRenderer.render(texts);
    }

    public static void loadText(GuiText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GuiText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public static void loadTexture(GuiTexture texture) {
        textures.add(texture);
    }

    public static void removeText(GuiText text) {
        List<GuiText> textBatch = texts.get(text.getFont());
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
