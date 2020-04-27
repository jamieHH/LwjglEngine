package particles;

public class ParticleTexture {

    private int textureID;
    private int numberOfRows = 1;

    public ParticleTexture(int textureID) {
        this.textureID = textureID;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }
}
