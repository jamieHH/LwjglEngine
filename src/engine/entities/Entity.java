package engine.entities;

import engine.models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import engine.utils.sortAndPrune.Box;
import engine.utils.sortAndPrune.EndPoint;

public class Entity extends Point {

    private Vector3f velocity = new Vector3f(0, 0, 0);
    private TexturedModel model;
    private float scale;
    private Box boundingBox = null;
    private boolean checkBounds = false;
    private boolean isSolid = false;

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

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setSolid(boolean solid) {
        isSolid = solid;
    }

    @Override
    public void setPosition(Vector3f position) {
        super.setPosition(position);
        checkBounds = true;
    }

    @Override
    public void movePosition(float nx, float ny, float nz) {
        super.movePosition(nx, ny, nz);
        checkBounds = true;
    }

    @Override
    public void setPosX(float posX) {
        super.setPosX(posX);
        checkBounds = true;
    }

    @Override
    public void movePosX(float moveX) {
        super.movePosX(moveX);
        checkBounds = true;
    }

    @Override
    public void setPosY(float posY) {
        super.setPosY(posY);
        checkBounds = true;
    }

    @Override
    public void movePosY(float moveY) {
        super.movePosY(moveY);
        checkBounds = true;
    }

    @Override
    public void setPosZ(float posZ) {
        super.setPosZ(posZ);
        checkBounds = true;
    }

    @Override
    public void movePosZ(float moveZ) {
        super.movePosZ(moveZ);
        checkBounds = true;
    }

    public boolean isCheckBounds() {
        return checkBounds;
    }

    public void setCheckBounds(boolean checkBounds) {
        this.checkBounds = checkBounds;
    }
}
