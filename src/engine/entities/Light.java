package engine.entities;

import org.lwjgl.util.vector.Vector3f;

public class Light extends Point {

	private Vector3f color;
	private float intensity;

	public Light(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}

	public Vector3f getColor() {
		return color;
	}
}
