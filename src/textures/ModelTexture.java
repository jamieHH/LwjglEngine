package textures;

public class ModelTexture {
	
	private int textureID;
	private int normalMapID;
	private float shineDamper = 1;
	private float reflectivity = 0;
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean hasNormalMap = false;


	public ModelTexture(int texture) {
		this.textureID = texture;
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
		return hasNormalMap;
	}

	public void setIsHasNormalMap(boolean b) {
		this.hasNormalMap = b;
	}

	public int getNormalMapID() {
		return normalMapID;
	}

	public void setNormalMapID(int normalMapID) {
		this.normalMapID = normalMapID;
	}
}
