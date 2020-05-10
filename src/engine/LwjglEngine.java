package engine;

import engine.guis.fontLoader.FontType;
import engine.guis.fontLoader.GuiText;
import engine.loaders.Loader;
import engine.renderEngine.GuiMasterRenderer;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;

public class LwjglEngine implements Runnable {

    private static IGameLogic gameLogic;
    private final Thread gameLoopThread;
    private static String title;
    private static int width;
    private static int height;

    public LwjglEngine(String title, int width, int height, int samples, IGameLogic gameLogic) {
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        LwjglEngine.gameLogic = gameLogic;
        LwjglEngine.title = title;
        LwjglEngine.width = width;
        LwjglEngine.height = height;
    }

    public void init() {
        DisplayManager.createDisplay(width, height, title);
        gameLogic.init();
    }

    public void start() {
        gameLoopThread.start();
    }

	public void run() {
        init();
        gameLoop();
        cleanUp();
	}

	private void gameLoop() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;
        while (!Display.isCloseRequested()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            boolean ticked = false;
            lastTime = now;
            while (delta >= 1) {
                ticked = true;
                tick();
                updates++;
                delta--;
            }
            if (ticked) {
                render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                GuiMasterRenderer.clearText();
                FontType font = new FontType(Loader.loadFontTexture("font/arial"), new File("res/font/arial.fnt"));
                GuiText guiText = new GuiText("FPS: " + frames + " | " + "UPS: " + updates, 1f, font, new Vector2f(0f, 0f), 1f, false);
                GuiMasterRenderer.loadText(guiText);
                frames = 0;
                updates = 0;
            }
        }
    }

    private void tick() {
        gameLogic.tick();
    }

	private void render() {
        gameLogic.render();
        DisplayManager.updateDisplay();
    }

    public void cleanUp() {
        gameLogic.cleanUp();
        DisplayManager.closeDisplay();
    }
}
