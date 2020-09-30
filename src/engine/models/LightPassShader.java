package engine.models;

import engine.entities.EnvLight;
import engine.entities.Light;
import engine.renderEngine.ShaderProgram;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class LightPassShader extends ShaderProgram {

    private static final int MAX_LIGHTS = 16;
    private static final int MAX_ENV_LIGHTS = 2;
    private static final Vector3f NULL_VECTOR = new Vector3f(0, 0, 0);

    private static final String VERTEX_FILE = "src/engine/bloom/simpleVertex.txt";
    private static final String FRAGMENT_FILE = "src/engine/models/lightPassFragmentShader.glsl";

    private int location_positionsFrame;
    private int location_normalsFrame;
    private int location_albedoFrame;
    private int location_specularFrame;
    private int location_viewPos;
    private int[] location_lightColor;
    private int[] location_lightPosition;
    private int[] location_envLightColor;
    private int[] location_envLightDirection;
    private int location_skyColor;

    public LightPassShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "transformationMatrix");
    }

    @Override
    protected void getAllUniformLocations() {
        location_positionsFrame = super.getUniformLocation("positionsFrame");
        location_normalsFrame = super.getUniformLocation("normalsFrame");
        location_albedoFrame = super.getUniformLocation("albedoFrame");
        location_specularFrame = super.getUniformLocation("specularFrame");
        location_viewPos = super.getUniformLocation("viewPos");
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

    public void loadViewPos(Vector3f viewPos) {
        super.loadVector(location_viewPos, viewPos);
    }

    protected void connectTextureUnits(){
        super.loadInt(location_positionsFrame, 0);
        super.loadInt(location_normalsFrame, 1);
        super.loadInt(location_albedoFrame, 2);
        super.loadInt(location_specularFrame, 3);
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
}
