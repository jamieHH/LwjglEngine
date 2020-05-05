package engine.models;

import engine.entities.EnvLight;
import engine.entities.Point;
import engine.renderEngine.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector3f;
import engine.utils.Maths;

import engine.entities.Light;

import java.util.List;

public class ModelShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 16;
	private static final int MAX_ENV_LIGHTS = 2;
	private static final Vector3f NULL_VECTOR = new Vector3f(0, 0, 0);

	private static final String VERTEX_FILE = "src/engine/models/vertexShader.txt";
	private static final String FRAGMENT_FILE = "src/engine/models/fragmentShader.txt";

	private int location_projectionMatrix;
	private int[] location_lightColor;
	private int[] location_lightPosition;
	private int[] location_envLightColor;
	private int[] location_envLightDirection;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;

	public ModelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "transformationMatrix");
        super.bindAttribute(7, "viewMatrix");
    }

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
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
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
}
