package engine.postProcessing;

import engine.renderEngine.ShaderProgram;

public class ContrastShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/engine/postProcessing/contrastVertex.txt";
	private static final String FRAGMENT_FILE = "src/engine/postProcessing/contrastFragment.txt";

	private static int location_contrast;
	
	public ContrastShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_contrast = super.getUniformLocation("contrast");
	}

	public void loadContrast(float contrast) {
		super.loadFloat(location_contrast, contrast);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
