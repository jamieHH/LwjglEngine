package textures;

public class ModelTexture {
	
	private int textureID;
	private int normalMapID;
	private float shineDamper = 1;
	private float reflectivity = 0;
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean hasNormalMap;


	public ModelTexture(int texture) {
		this.textureID = texture;
		this.hasNormalMap = false;
	}

	public ModelTexture(int texture, int normalMap) {
		this.textureID = texture;
		this.normalMapID = normalMap;
		this.hasNormalMap = true;
	}
	
	public int getID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public boolean isHasNormalMap() {
		return this.hasNormalMap;
	}

	public int getNormalMapID() {
		return this.normalMapID;
	}
}
