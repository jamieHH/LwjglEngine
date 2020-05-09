package engine.skybox;

import engine.entities.Point;
import engine.models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engine.loaders.Loader;

public class SkyboxRenderer {

    private static final float SIZE = 1000f;
    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static String[] DAY_TEXTURE_FILES = {
            "skybox/day/right",
            "skybox/day/left",
            "skybox/day/top",
            "skybox/day/bottom",
            "skybox/day/back",
            "skybox/day/front",
    };
    private static String[] NIGHT_TEXTURE_FILES = {
            "skybox/night/right",
            "skybox/night/left",
            "skybox/night/top",
            "skybox/night/bottom",
            "skybox/night/back",
            "skybox/night/front",
    };

    private static RawModel cube;
    private static int dayTexID;
    private static int nightTexID;
    private static SkyboxShader shader = new SkyboxShader();

    public static void init(Matrix4f projectionMatrix) {
        cube = Loader.loadToVAO(VERTICES, 3);
        dayTexID = Loader.loadCubeMap(DAY_TEXTURE_FILES);
        nightTexID = Loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public static void render(Point camera, Vector3f skyColor) {
        prepare(camera, skyColor);
        // draw call
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        finish();
    }

    private static void prepare(Point camera, Vector3f skyColor) {
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadFogColor(skyColor);
        shader.loadBlendFactor(0.1f);
        // bind vertexes
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        // bind engine.textures
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, dayTexID);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTexID);
    }

    private static void finish() {
        // unbind vertexes
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public static void cleanUp(){
        shader.cleanUp();
    }
}
