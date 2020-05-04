package engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {

	public static void createDisplay(int width, int height, int samples, String title) {
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			if (samples > 1) {
				Display.create(new PixelFormat().withSamples(samples), attribs);
				GL11.glEnable(GL13.GL_MULTISAMPLE);
			} else {
				Display.create(new PixelFormat(), attribs);
			}
			Display.setTitle(title);
		} catch (LWJGLException e) {
			System.out.println("Could not create the display!");
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, width, height);
	}
	
	public static void updateDisplay() {
		Display.sync(240);
		Display.update();
	}
	
	public static void closeDisplay() {
		Display.destroy();
	}
}
