package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light extends Point {

	private Vector3f color;

    public Light(Vector3f color) {
        this.color = color;
    }

	public Vector3f getColor() {
		return color;
	}
}
