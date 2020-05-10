package engine.bloom;

import engine.renderEngine.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/engine/bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "src/engine/bloom/combineFragment.txt";
	
	private int location_colorTexture;
	private int location_highlightTexture;
	private int location_blendFactor;

	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_colorTexture = super.getUniformLocation("colorTexture");
		location_highlightTexture = super.getUniformLocation("highlightTexture");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_colorTexture, 0);
		super.loadInt(location_highlightTexture, 1);
	}

	public void loadBlendFactor(float blendFactor) {
		super.loadFloat(location_blendFactor, blendFactor);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
