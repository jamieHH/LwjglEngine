package engineTester.assets;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import textures.ModelTexture;

public class Models {

    public static TexturedModel chair = new TexturedModel(makeModelWithTangents("chair0"),
            makeModelTexture("wood"));
    public static TexturedModel rock = new TexturedModel(makeModelWithTangents("rock0HD"),
            makeModelTexture("rock", 0.5f, 1.0f));
    public static TexturedModel grass = new TexturedModel(makeModelWithTangents("grass0"),
            makeTransparentModelTexture("grass"));
    public static TexturedModel lamp = new TexturedModel(makeModelWithTangents("lampPost0"),
            makeModelTexture("steel", 0.5f, 1.0f));
    public static TexturedModel lightTest = new TexturedModel(makeRawModel("lightTest"),
            makeModelTexture("steel"));
    public static TexturedModel barrelModel = new TexturedModel(makeModelWithTangents("barrel"),
            makeModelTexture("barrel", "barrelNormal", 1f, 10f));

    private static RawModel makeRawModel(String objFileName) {
        return OBJFileLoader.loadOBJ(objFileName);
    }

    private static RawModel makeModelWithTangents(String objFileName) {
        return NormalMappedObjLoader.loadOBJ(objFileName);
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

    private static ModelTexture makeModelTexture(String textureFileName, String normalMapFileName, float reflectivity, float shineDamper) {
        ModelTexture texture = new ModelTexture(Loader.loadTexture(textureFileName), Loader.loadTexture(normalMapFileName));
        texture.setReflectivity(reflectivity);
        texture.setShineDamper(shineDamper);
        return texture;
    }
}
