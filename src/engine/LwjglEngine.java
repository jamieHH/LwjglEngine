package engine;

import engine.guis.fontLoader.FontType;
import engine.guis.fontLoader.GuiText;
import engine.loaders.Loader;
import engine.renderEngine.GuiMasterRenderer;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;

public class LwjglEngine {

    private static IGameLogic gameLogic;

    public LwjglEngine(String title, int width, int height, int samples, IGameLogic gameLogic) {
        DisplayManager.createDisplay(width, height, samples, title);
        LwjglEngine.gameLogic = gameLogic;
    }

    public void init() {
        gameLogic.init();
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
            lastTime = now;
            while (delta >= 1) {
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;

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
