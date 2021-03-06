package engine.terrains;

import engine.entities.EnvLight;
import engine.entities.Light;
import engine.entities.Point;
import engine.renderEngine.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engine.utils.Maths;

import java.util.List;

public class TerrainShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 8;
	private static final int MAX_ENV_LIGHTS = 2;
	private static final Vector3f NULL_VECTOR = new Vector3f(0, 0, 0);

	private static final String VERTEX_FILE = "src/engine/terrains/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/engine/terrains/terrainFragmentShader.txt";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightColor;
	private int[] location_lightPosition;
	private int[] location_envLightColor;
	private int[] location_envLightDirection;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColor;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColor = super.getUniformLocation("skyColor");
        location_lightColor = new int [MAX_LIGHTS];
        location_lightPosition = new int [MAX_LIGHTS];
		location_envLightColor = new int [MAX_ENV_LIGHTS];
		location_envLightDirection = new int [MAX_ENV_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
		}
		for (int i = 0; i < MAX_ENV_LIGHTS; i++) {
			location_envLightColor[i] = super.getUniformLocation("envLightColor[" + i + "]");
			location_envLightDirection[i] = super.getUniformLocation("envLightDirection[" + i + "]");
		}

		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
	}

	public void connectTextureUnits() {
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
	}

	public void loadSkyColor(Vector3f color) {
		super.loadVector(location_skyColor, color);
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
                super.loadVector(location_lightColor[i], lights.get(i).getColor());
                super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
			} else {
                super.loadVector(location_lightColor[i], NULL_VECTOR);
                super.loadVector(location_lightPosition[i], NULL_VECTOR);
			}
		}
	}

	public void loadEnvLights(List<EnvLight> lights) {
		for (int i = 0; i < MAX_ENV_LIGHTS; i++) {
			if (i < lights.size()) {
                super.loadVector(location_envLightColor[i], lights.get(i).getColor());
                super.loadVector(location_envLightDirection[i], lights.get(i).getDirection());
			} else {
                super.loadVector(location_envLightColor[i], NULL_VECTOR);
                super.loadVector(location_envLightDirection[i], NULL_VECTOR);
			}
		}
	}
	
	public void loadViewMatrix(Point point) {
		Matrix4f viewMatrix = Maths.createViewMatrix(point);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	

}
