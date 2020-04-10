package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	protected Vector3f position = new Vector3f(0,2,0);
	protected float pitch;
	protected float yaw;
	protected float roll;
	
	public Camera() {

	}
	
	public void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.z -= 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x -= 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.z += 0.02f;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			yaw -= 0.5f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			yaw += 0.5f;
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
