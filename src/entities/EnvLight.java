package entities;

import org.lwjgl.util.vector.Vector3f;

public class EnvLight extends Light {

    public EnvLight(Vector3f color, Vector3f direction) {
        super(color);
        super.setRotation(direction);
    }

    public Vector3f getDirection() {
        return getRotation();
    }
}