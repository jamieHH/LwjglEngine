package engine.particles;

public class ParticleTexture {

    private int textureId;
    private int numberOfRows = 1;

    public ParticleTexture(int textureId) {
        this.textureId = textureId;
    }

    public int getTextureId() {
        return textureId;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }
}
