package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {
	
//	private static final int WIDTH = 1280;
//	private static final int HEIGHT = 720;
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
//	private static final int WIDTH = 2560;
//	private static final int HEIGHT = 1440;
	private static final int FPS_CAP = 240;

	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withSamples(16), attribs);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			Display.setTitle("LWJGL Engine");
		} catch (LWJGLException e) {
			System.out.println("Could not create the display!");
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, WIDTH, HEIGHT);
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
}
