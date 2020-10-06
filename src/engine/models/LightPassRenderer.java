package engine.models;

import engine.entities.EnvLight;
import engine.entities.Light;
import engine.entities.Point;
import engine.loaders.Loader;
import engine.postProcessing.Fbo;
import engine.postProcessing.ImageRenderer;
import engine.renderEngine.WorldMasterRenderer;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class LightPassRenderer {

    private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
    private static RawModel quad;

    private static ImageRenderer renderer;
    private static LightPassShader shader;

    private static Fbo normalsFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo specularFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo albedoFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo positionFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

    public static void init() {
        quad = Loader.loadToVAO(POSITIONS, 2);

        shader = new LightPassShader();
        shader.start();
        shader.connectTextureUnits();
        shader.stop();
        renderer = new ImageRenderer(Display.getWidth(), Display.getHeight());
    }

    public static void render(List<Light> pointLights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
        // start-render
        GL30.glBindVertexArray(quad.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // prepare
        shader.start();
        shader.loadViewPos(camera.getPosition());
        shader.loadSkyColor(skyColor);
        shader.loadEnvLights(envLights);
        shader.loadLights(pointLights);


        // main
        WorldMasterRenderer.baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, normalsFbo);
        WorldMasterRenderer.baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT2, specularFbo);
        WorldMasterRenderer.baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT3, albedoFbo);
        WorldMasterRenderer.baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT4, positionFbo);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, positionFbo.getColorTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalsFbo.getColorTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, albedoFbo.getColorTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, specularFbo.getColorTexture());

        renderer.renderQuad();

        // finish
        shader.stop();

        //end render
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public static int getOutputTexture(){
        return renderer.getOutputTexture();
    }

    public static void cleanUp(){
        renderer.cleanUp();
        shader.cleanUp();
    }
}
