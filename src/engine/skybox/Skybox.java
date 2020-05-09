package engine.skybox;

import engine.loaders.Loader;

public class Skybox {

    private String[] primaryTextures;
    private String[] secondaryTextures;

    private int primaryCubemapId;
    private int secondaryCubemapId;

    public Skybox(String[] primaryTextures, String[] secondaryTextures) {
        primaryCubemapId = Loader.loadCubeMap(primaryTextures);
        secondaryCubemapId = Loader.loadCubeMap(secondaryTextures);
    }

    public int getPrimaryCubemapId() {
        return primaryCubemapId;
    }

    public int getSecondaryCubemapId() {
        return secondaryCubemapId;
    }
}
