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

//    public static void main(String[] args) {
//        new EngineTester().run();
//    }

//	public void run() {
//        init();
//        gameLoop();
//	}

//	public void gameLoop() {
//        long lastTime = System.nanoTime();
//        double amountOfTicks = 60.0;
//        double ns = 1000000000 / amountOfTicks;
//        double delta = 0;
//        long timer = System.currentTimeMillis();
//        int frames = 0;
//        int updates = 0;
//        while (!Display.isCloseRequested()) {
//            long now = System.nanoTime();
//            delta += (now - lastTime) / ns;
//            lastTime = now;
//            while (delta >= 1) {
//                tick();
//                updates++;
//                delta--;
//            }
//            render();
//            frames++;
//
//            if (System.currentTimeMillis() - timer > 1000000000) {
//                timer += 1000;
//                GuiMasterRenderer.clearText();
//                FontType font = new FontType(Loader.loadFontTexture("font/arial"), new File("res/font/arial.fnt"));
//                GuiText guiText = new GuiText("FPS: " + frames + " | " + "UPS: " + updates, 1f, font, new Vector2f(0f, 0f), 1f, false);
//                GuiMasterRenderer.loadText(guiText);
//                frames = 0;
//                updates = 0;
//            }
//        }
//    }

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
        WorldMasterRenderer.render();
        GuiMasterRenderer.render();
    }

    public void cleanUp() {
        WorldMasterRenderer.cleanUp();
        GuiMasterRenderer.cleanUp();
        Loader.cleanUp();
    }
}