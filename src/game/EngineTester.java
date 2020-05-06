package game;

import engine.IGameLogic;
import engine.entities.*;
import engine.guis.GuiTexture;
import engine.guis.fontLoader.FontType;
import engine.guis.fontLoader.GuiText;
import engine.loaders.Loader;
import engine.renderEngine.GuiMasterRenderer;
import engine.renderEngine.WorldMasterRenderer;
import engine.utils.MousePicker;
import engine.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;

public class EngineTester implements IGameLogic {

    private static World world;
    private static Camera camera;
    private static MousePicker picker;
    private int lampWait = 0;

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
        GuiTexture guiTexture = new GuiTexture(Loader.loadTexture("grass"), new Vector2f(-0.75f, 0.75f), new Vector2f(0.125f, 0.125f));
        GuiMasterRenderer.loadTexture(guiTexture);

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
                if (Mouse.isButtonDown(0)) {
                    // modify heights
                    Vector3f tv = world.getTerrain().worldToTerrainVector(tp);
                    float radius = 5;
                    float amount = 0.5f;
                    for (int x = 0; x < radius; x++) {
                        for (int z = 0; z < radius; z++) {
                            world.getTerrain().heights[(int) (tv.getX() + x)][(int) (tv.getZ() + z)] += amount;
                        }
                    }
                    world.getTerrain().updateTerrain();

                    // place lamp
//                    lampWait = 15;
//                    world.addLight(
//                            new Light(new Vector3f(0, 1, 0), 1f),
//                            tp.getX(), tp.getY() + 8, tp.getZ()
//                    );
//                    world.addEntity(
//                            new Entity(Models.lamp, 1),
//                            tp.getX(), tp.getY(), tp.getZ(),
//                            0, 0, 0
//                    );
                }
            }
        }
    }

	public void render() {
        WorldMasterRenderer.render();
        GuiMasterRenderer.render();
    }

    public void cleanUp() {
        WorldMasterRenderer.cleanUp();
        GuiMasterRenderer.cleanUp();
        Loader.cleanUp();
    }
}
