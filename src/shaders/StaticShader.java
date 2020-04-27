package shaders;

import entities.EnvLight;
import entities.Point;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

import entities.Light;

import java.util.List;

public class StaticShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 8;
	private static final int MAX_ENV_LIGHTS = 2;

	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColor;
	private int[] location_attenuation;
	private int[] location_envLightColor;
	private int[] location_envLightDirection;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;

	public StaticShader() {
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
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_lightPosition = new int [MAX_LIGHTS];
		location_lightColor = new int [MAX_LIGHTS];
		location_attenuation = new int [MAX_LIGHTS];
		location_envLightColor = new int [MAX_ENV_LIGHTS];
		location_envLightDirection = new int [MAX_ENV_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		for (int i = 0; i < MAX_ENV_LIGHTS; i++) {
			location_envLightColor[i] = super.getUniformLocation("envLightColor[" + i + "]");
			location_envLightDirection[i] = super.getUniformLocation("envLightDirection[" + i + "]");
		}
	}

	public void loadSkyColor(Vector3f color) {
	    super.loadVector(location_skyColor, color);
    }

	public void loadFakeLightingVariariable(boolean useFakeLighting) {
		super.loadBoolean(location_useFakeLighting, useFakeLighting);
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
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i], lights.get(i).getColor());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}

	public void loadEnvLights(List<EnvLight> lights) {
		for (int i = 0; i < MAX_ENV_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(location_envLightDirection[i], lights.get(i).getDirection());
				super.loadVector(location_envLightColor[i], lights.get(i).getColor());
			} else {
				super.loadVector(location_envLightDirection[i], new Vector3f(0, 0, 0));
				super.loadVector(location_envLightColor[i], new Vector3f(0, 0, 0));
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
