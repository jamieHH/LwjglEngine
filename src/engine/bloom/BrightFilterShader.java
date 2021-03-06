package engine.bloom;

import engine.renderEngine.ShaderProgram;

public class BrightFilterShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "src/engine/bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "src/engine/bloom/brightFilterFragment.txt";

	private int location_threshold;
	
	public BrightFilterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_threshold = super.getUniformLocation("threshold");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadThreshold(float threshold) {
		super.loadFloat(location_threshold, threshold);
	}
}
