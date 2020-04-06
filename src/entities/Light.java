package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Light {
	
	public Vector3f position;
	private Vector3f color;
	
	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
	}

	public void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			position.z -= 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			position.x += 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			position.x -= 0.02f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			position.z += 0.02f;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			position.y += 0.05f;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			position.y -= 0.05f;
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public Vector3f getColor() {
		return color;
	}
	public void setColor(Vector3f color) {
		this.color = color;
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

}
