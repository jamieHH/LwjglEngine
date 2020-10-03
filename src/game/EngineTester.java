package game;

import engine.IGameLogic;
import engine.entities.*;
import engine.guis.GuiTexture;
import engine.loaders.Loader;
import engine.models.DeferredModelRenderer;
import engine.postProcessing.Fbo;
import engine.postProcessing.PostProcessing;
import engine.renderEngine.GuiMasterRenderer;
import engine.renderEngine.WorldMasterRenderer;
import engine.utils.MousePicker;
import engine.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class EngineTester implements IGameLogic {

    private static World world;
    private static Camera camera;
    private static MousePicker picker;
    private int lampWait = 0;

    private static Fbo baseFbo;
    private static Fbo normalsFbo;
    private static Fbo specularFbo;
    private static Fbo albedoFbo;
    private static Fbo positionFbo;
    private static Fbo outputFbo;

    public EngineTester() {

    }

    public void init() {
        world = new TestWorld();

        Player player = new Player(Models.chair, 1);
        player.setRotation(new Vector3f(0, 45, 0));
        world.addEntity(player, 0, 0, 0);
//        camera = new OrbitalCamera(player);

        camera = new Camera();
        camera.setPosition(new Vector3f(0f, 20f, 0f));
        camera.setRotation(new Vector3f(0f, 135f, 0f));
        camera.setWorld(world);

        WorldMasterRenderer.init(world, camera);
        GuiMasterRenderer.init();
//        GuiTexture guiTexture = new GuiTexture(Loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f));
//        GuiMasterRenderer.loadTexture(guiTexture);

        baseFbo = new Fbo(Display.getWidth(), Display.getHeight());
        normalsFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        specularFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        albedoFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        positionFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init();

        GuiMasterRenderer.loadTexture(new GuiTexture(normalsFbo.getColorTexture(), new Vector2f(-0.75f, 0.75f), new Vector2f(0.25f, 0.25f))); // getting texture form normals vbo
        GuiMasterRenderer.loadTexture(new GuiTexture(specularFbo.getColorTexture(), new Vector2f(-0.25f, 0.75f), new Vector2f(0.25f, 0.25f))); // getting texture form specular vbo
        GuiMasterRenderer.loadTexture(new GuiTexture(albedoFbo.getColorTexture(), new Vector2f(0.25f, 0.75f), new Vector2f(0.25f, 0.25f)));
        GuiMasterRenderer.loadTexture(new GuiTexture(positionFbo.getColorTexture(), new Vector2f(0.75f, 0.75f), new Vector2f(0.25f, 0.25f)));

        GuiMasterRenderer.loadTexture(new GuiTexture(DeferredModelRenderer.baseFbo.getColorTexture(), new Vector2f(0f, 0f), new Vector2f(0.25f, 0.25f)));

        picker = new MousePicker(camera, WorldMasterRenderer.getProjectionMatrix(), world.getTerrain());
    }

    public void tick() {
        world.tick();
        camera.tick();

        picker.update();
        Vector3f tp = picker.getCurrentTerrainPoint();
        if (tp != null) {
            if (lampWait > 0) {
                lampWait--;
            } else {
                if (Mouse.isButtonDown(2)) {
                    // modify heights
                    Vector3f tv = world.getTerrain().worldToTerrainVector(tp);
                    float radius = 40;
                    float amount = 0.5f;
                    for (int x = (int) (-radius/2); x < radius/2; x++) {
                        for (int z = (int) (-radius/2); z < radius/2; z++) {
                            world.getTerrain().moveTerrainHeight((int) (tv.getX() + x), (int) (tv.getZ() + z), amount);
                        }
                    }
                    world.getTerrain().updateTerrain();

                }
                if (Mouse.isButtonDown(0)) {
                    //place lamp
                    lampWait = 15;
                    world.addLight(
                            new Light(new Vector3f(0, 1, 0), 1f),
                            tp.getX(), tp.getY() + 8, tp.getZ()
                    );
                    world.addEntity(
                            new Entity(Models.lamp, 1),
                            tp.getX(), tp.getY(), tp.getZ(),
                            0, 0, 0
                    );
                }
            }
        }
    }

	public void render() {
        baseFbo.bindFrameBuffer();
        WorldMasterRenderer.render();
        baseFbo.unbindFrameBuffer();

        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, normalsFbo); // resolving attachment1 to the normals fbo
        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT2, specularFbo);
        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT3, albedoFbo);
        baseFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT4, positionFbo);

        outputFbo.resolveToScreen();
//        PostProcessing.doPostProcessing(outputFbo.getColorTexture());

        GuiMasterRenderer.render();
    }

    public void cleanUp() {
        PostProcessing.cleanUp();
        baseFbo.cleanUp();
        normalsFbo.cleanUp();
        specularFbo.cleanUp();
        albedoFbo.cleanUp();
        specularFbo.cleanUp();
        positionFbo.cleanUp();
        WorldMasterRenderer.cleanUp();
        GuiMasterRenderer.cleanUp();
        Loader.cleanUp();
    }
}
