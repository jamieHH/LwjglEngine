package engine.models;

import engine.entities.*;

import engine.loaders.Loader;
import engine.postProcessing.Fbo;
import engine.postProcessing.ImageRenderer;
import engine.renderEngine.WorldMasterRenderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector3f;
import engine.utils.Maths;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

public class DeferredModelRenderer {

    private static final int MAX_INSTANCES = 100000;
    private static final int INSTANCE_DATA_LENGTH = 16;
    private static FloatBuffer buffer;

    private static int vbo;
    private static int pointer = 0;

    private static ImageRenderer imageRenderer = new ImageRenderer(Display.getWidth(), Display.getHeight());
    private static DeferredModelShader shader = new DeferredModelShader();
    private static LightPassShader lightPassShader = new LightPassShader();

    public static Fbo baseFbo = new Fbo(Display.getWidth(), Display.getHeight());
    public static Fbo testFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo normalsFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo specularFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo albedoFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
    private static Fbo positionFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

    public static void init(Matrix4f projectionMatrix) {
        vbo = Loader.createFloatVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();

        lightPassShader.start();
        lightPassShader.connectTextureUnits();
        lightPassShader.stop();
    }

    public static void render(Map<TexturedModel, List<Entity>> entities, List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Point camera) {
//        baseFbo.bindFrameBuffer();

        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        prepare(lights, envLights, skyColor, viewMatrix);
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            pointer = 0;
            float[] vboData = new float[batch.size() * INSTANCE_DATA_LENGTH];
            buffer = BufferUtils.createFloatBuffer(batch.size() * INSTANCE_DATA_LENGTH);
            for (Entity entity : batch) {
                prepareInstance(entity, vboData);
            }
            Loader.updateVbo(vbo, vboData, buffer);
            GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0, batch.size());
            unbindTexturedModel();
        }
        finish();

//        baseFbo.unbindFrameBuffer(); // TODO: need to get all the color attachments fot the pic above


        
//        // lighting pass
//        // start
//        GL30.glBindVertexArray(PostProcessing.getQuad().getVaoId());
//        GL20.glEnableVertexAttribArray(0);
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
//
//        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, testFbo); // resolving attachment1 to the normals fbo
//        // prepare
//        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, normalsFbo); // resolving attachment1 to the normals fbo
//        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT2, specularFbo);
//        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT3, albedoFbo);
//        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT4, positionFbo);
//
//        lightPassShader.start();
//        lightPassShader.connectTextureUnits();
//        lightPassShader.loadSkyColor(skyColor);
//        lightPassShader.loadViewPos(camera.getPosition());
//        lightPassShader.loadEnvLights(envLights);
//        lightPassShader.loadLights(lights);
//
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, positionFbo.getColorTexture());
//        GL13.glActiveTexture(GL13.GL_TEXTURE1);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalsFbo.getColorTexture());
//        GL13.glActiveTexture(GL13.GL_TEXTURE2);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, albedoFbo.getColorTexture());
//        GL13.glActiveTexture(GL13.GL_TEXTURE3);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, specularFbo.getColorTexture());
//        // or --
////        lightPassShader.loadGBufferFrames(positionFbo.getColorTexture(), normalsFbo.getColorTexture(), albedoFbo.getColorTexture(), specularFbo.getColorTexture());
//
//        imageRenderer.renderQuad();
//        lightPassShader.stop();
//
//
//        // finish
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL20.glDisableVertexAttribArray(0);
//        GL30.glBindVertexArray(0);
    }

    private static void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();

        Loader.addInstancedAttribute(rawModel.getVaoId(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 0);
        Loader.addInstancedAttribute(rawModel.getVaoId(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 4);
        Loader.addInstancedAttribute(rawModel.getVaoId(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 8);
        Loader.addInstancedAttribute(rawModel.getVaoId(), vbo, 6, 4, INSTANCE_DATA_LENGTH, 12);
        Loader.addInstancedAttribute(rawModel.getVaoId(), vbo, 7, 4, INSTANCE_DATA_LENGTH, 16);

        // bind vertexes
        GL30.glBindVertexArray(rawModel.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);
        GL20.glEnableVertexAttribArray(5);
        GL20.glEnableVertexAttribArray(6);
        ModelTexture texture = model.getTexture();
        if (texture.isHasTransparency()) {
            WorldMasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        // bind engine.textures
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    private static void prepareInstance(Entity entity, float[] vboData) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotation(), entity.getScale());
        storeMatrixData(transformationMatrix, vboData);
    }

    private static void storeMatrixData(Matrix4f matrix, float[] vboData) {
        vboData[pointer++] = matrix.m00;
        vboData[pointer++] = matrix.m01;
        vboData[pointer++] = matrix.m02;
        vboData[pointer++] = matrix.m03;
        vboData[pointer++] = matrix.m10;
        vboData[pointer++] = matrix.m11;
        vboData[pointer++] = matrix.m12;
        vboData[pointer++] = matrix.m13;
        vboData[pointer++] = matrix.m20;
        vboData[pointer++] = matrix.m21;
        vboData[pointer++] = matrix.m22;
        vboData[pointer++] = matrix.m23;
        vboData[pointer++] = matrix.m30;
        vboData[pointer++] = matrix.m31;
        vboData[pointer++] = matrix.m32;
        vboData[pointer++] = matrix.m33;
    }

    private static void prepare(List<Light> lights, List<EnvLight> envLights, Vector3f skyColor, Matrix4f viewMatrix) {
        shader.start();
        shader.loadViewMatrix(viewMatrix);
        shader.loadSkyColor(skyColor);
        shader.loadEnvLights(envLights);
        shader.loadLights(lights);
    }

    private static void finish() {
        shader.stop();
    }

    private static void unbindTexturedModel() {
        WorldMasterRenderer.enableCulling();
        // unbind vertexes
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL20.glDisableVertexAttribArray(5);
        GL20.glDisableVertexAttribArray(6);
        GL30.glBindVertexArray(0);
    }

    public static void cleanUp(){
        shader.cleanUp();
    }
}
