package entities;

import models.TexturedModel;
import toolbox.sortAndPrune.Box;
import toolbox.sortAndPrune.EndPoint;

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

	public Box getBoundingBox(int id) {
		return new Box(id,
				new EndPoint[] {
						new EndPoint(id, getPosX() -1, true),
						new EndPoint(id, getPosY() -1, true),
						new EndPoint(id, getPosZ() -1, true)
				},
				new EndPoint[] {
						new EndPoint(id, getPosX() +1, false),
						new EndPoint(id, getPosY() +1, false),
						new EndPoint(id, getPosZ() +1, false)
				}

		);
	}
}
