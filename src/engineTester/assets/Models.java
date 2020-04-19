package engineTester.assets;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import textures.ModelTexture;

public class Models {

    public static TexturedModel chair = new TexturedModel(makeRawModel("chair0"), makeModelTexture("wood"));
    public static TexturedModel rock = new TexturedModel(makeRawModel("rock0HD"), makeModelTexture("rock", 0.5f, 1.0f));
    public static TexturedModel grass = new TexturedModel(makeRawModel("grass0"), makeTransparentModelTexture("grass"));
    public static TexturedModel lamp = new TexturedModel(makeRawModel("lampPost0"), makeModelTexture("steel", 0.5f, 1.0f));
    public static TexturedModel lightTest = new TexturedModel(makeRawModel("lightTest"), makeModelTexture("steel"));

    private static RawModel makeRawModel(String objFileName) {
        ModelData modelData = OBJFileLoader.loadOBJ(objFileName);
        return Loader.loadToVAO(
                modelData.getVertices(),
                modelData.getTextureCoords(),
                modelData.getNormals(),
                modelData.getIndices()
        );
    }

    private static ModelTexture makeModelTexture(String fileName) {
        ModelTexture texture = new ModelTexture(Loader.loadTexture(fileName));
        return texture;
    }

    private static ModelTexture makeTransparentModelTexture(String fileName) {
        ModelTexture texture = new ModelTexture(Loader.loadTexture(fileName));
        texture.setHasTransparency(true);
        texture.setUseFakeLighting(true);
        return texture;
    }

    private static ModelTexture makeModelTexture(String fileName, float reflectivity, float shineDamper) {
        ModelTexture texture = new ModelTexture(Loader.loadTexture(fileName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(shineDamper);
        return texture;
    }
}
