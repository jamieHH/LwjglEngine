package engineTester.assets;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import renderEngine.Loader;
import textures.ModelTexture;

public class Models {

    public static TexturedModel chair = new TexturedModel(makeRawModel("chair0"),
            makeModelTexture("wood"));
    public static TexturedModel rock = new TexturedModel(makeRawModel("rock0HD"),
            makeModelTexture("rock", 0.5f, 1.0f));
    public static TexturedModel grass = new TexturedModel(makeRawModel("grass0"),
            makeTransparentModelTexture("grass"));
    public static TexturedModel lamp = new TexturedModel(makeRawModel("lampPost0"),
            makeModelTexture("steel", 0.5f, 1.0f));
    public static TexturedModel lightTest = new TexturedModel(makeRawModel("lightTest"),
            makeModelTexture("steel"));
    public static TexturedModel barrelModel = new TexturedModel(makeRawModel("barrel"),
            makeModelTexture("barrel", "barrelNormal", 1f, 10f));


    private static RawModel makeRawModel(String objFileName) {
        return NormalMappedObjLoader.loadOBJ(objFileName);
    }

    private static ModelTexture makeModelTexture(String fileName) {
        return new ModelTexture(Loader.loadTexture(fileName));
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

    private static ModelTexture makeModelTexture(String textureFileName, String normalMapFileName, float reflectivity, float shineDamper) {
        ModelTexture texture = new ModelTexture(Loader.loadTexture(textureFileName), Loader.loadTexture(normalMapFileName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(shineDamper);
        return texture;
    }
}
