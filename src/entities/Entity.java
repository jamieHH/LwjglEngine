package entities;

import models.TexturedModel;

import org.lwjgl.util.vector.Vector3f;

public class Entity extends Point {

	private TexturedModel model;
	private float scale;

    public Entity(TexturedModel model, float scale) {
        this.model = model;
        this.scale = scale;
    }

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
}
