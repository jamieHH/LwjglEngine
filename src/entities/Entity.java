package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import toolbox.sortAndPrune.Box;
import toolbox.sortAndPrune.EndPoint;

public class Entity extends Point {

	private TexturedModel model;
	private float scale;
	private Box boundingBox = null;
	private boolean hasMoved = false;

    public Entity(TexturedModel model, float scale) {
        this.model = model;
        this.scale = scale;
    }

    public void tick() {

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

	public Box getBoundingBox() {
    	if (boundingBox != null) {
    		return boundingBox;
		} else {
			int id = this.hashCode();
			return new Box(id,
					new EndPoint[]{
							new EndPoint(id, getPosX() - 1, true),
							new EndPoint(id, getPosY() - 1, true),
							new EndPoint(id, getPosZ() - 1, true)
					},
					new EndPoint[]{
							new EndPoint(id, getPosX() + 1, false),
							new EndPoint(id, getPosY() + 1, false),
							new EndPoint(id, getPosZ() + 1, false)
					}

			);

		}
	}

    @Override
    public void setPosition(Vector3f position) {
        super.setPosition(position);
        hasMoved = true;
    }

    @Override
    public void movePosition(float nx, float ny, float nz) {
        super.movePosition(nx, ny, nz);
        hasMoved = true;
    }

    @Override
    public void setPosX(float posX) {
        super.setPosX(posX);
        hasMoved = true;
    }

    @Override
    public void movePosX(float moveX) {
        super.movePosX(moveX);
        hasMoved = true;
    }

    @Override
    public void setPosY(float posY) {
        super.setPosY(posY);
        hasMoved = true;
    }

    @Override
    public void movePosY(float moveY) {
        super.movePosY(moveY);
        hasMoved = true;
    }

    @Override
    public void setPosZ(float posZ) {
        super.setPosZ(posZ);
        hasMoved = true;
    }

    @Override
    public void movePosZ(float moveZ) {
        super.movePosZ(moveZ);
        hasMoved = true;
    }

    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}
