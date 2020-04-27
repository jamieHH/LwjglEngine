package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera extends Point {

	private static final float WALK_SPEED = 2f;
	private static final float FRICTION = 0.05f;

	private float forwardMove = 0;
	private float rightwardMove = 0;

	public Camera() {
		setPosition(new Vector3f(0,0,0));
	}

	public void tick() {
		calcPitch();
		calcYaw();

		float moveSpeed = WALK_SPEED * FRICTION;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) forwardMove += moveSpeed;
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) forwardMove -= moveSpeed;
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) rightwardMove -= moveSpeed;
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) rightwardMove += moveSpeed;

		float nx = (float) (forwardMove * Math.sin(Math.toRadians(-super.getRotY())) - rightwardMove * Math.cos(Math.toRadians(-super.getRotY())));
		float ny = (float) (forwardMove * Math.sin(Math.toRadians(-super.getRotX())));
		float nz = (float) (forwardMove * Math.cos(Math.toRadians(-super.getRotY())) + rightwardMove * Math.sin(Math.toRadians(-super.getRotY())));
		super.movePosition(-nx, ny, -nz);
		forwardMove *= 1 - FRICTION;
		rightwardMove *= 1 - FRICTION;
	}

	private void calcPitch() {
		if (Mouse.isButtonDown(0)) {
			float pitchDiff = Mouse.getDY() * 0.5f;
			if (getRotX() - pitchDiff < 90) {
				if (getRotX() - pitchDiff > -90) {
					setRotX(getRotX() - pitchDiff);
				} else {
					setRotX(-90);
				}
			} else {
				setRotX(90);
			}
		}
	}

	private void calcYaw() {
		if (Mouse.isButtonDown(0)) {
			float angleDiff = Mouse.getDX() * 0.5f;
			setRotY(getRotY() + angleDiff);
		}
	}
}
