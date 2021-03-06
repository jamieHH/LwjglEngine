package engine.entities;

import engine.models.TexturedModel;
import org.lwjgl.input.Keyboard;

public class Player extends Entity {

    private static final float WALK_SPEED = 0.5f;
    private static final float TURN_SPEED = 0.8f;
    private static final float JUMP_POWER = 1f;
    private static final float FRICTION = 0.25f;

    private float terrainHeight = 0;

    private float forwardMove = 0;
    private float rightwardMove = 0;
    private float upwardsMove = 0;
    private float rotationMove = 0;

    public Player(TexturedModel model, float scale) {
        super(model, scale);
        super.setSolid(true);
    }

    private boolean getIsFloating() {
        return super.getPosition().y > terrainHeight;
    }

    public void tick() {
        float moveSpeed = WALK_SPEED * FRICTION;
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) forwardMove += moveSpeed;
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) forwardMove -= moveSpeed;
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) rightwardMove -= moveSpeed;
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) rightwardMove += moveSpeed;
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!getIsFloating()) {
                this.upwardsMove = JUMP_POWER;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) rotationMove += TURN_SPEED;
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) rotationMove -= TURN_SPEED;

        float nx = (float) (forwardMove * Math.sin(Math.toRadians(super.getRotY())) - rightwardMove * Math.cos(Math.toRadians(super.getRotY())));
        float nz = (float) (forwardMove * Math.cos(Math.toRadians(super.getRotY())) + rightwardMove * Math.sin(Math.toRadians(super.getRotY())));
        super.getVelocity().translate(nx, 0, nz);
        forwardMove *= 1 - FRICTION;
        rightwardMove *= 1 - FRICTION;
        super.getVelocity().translate(0, upwardsMove, 0);
        upwardsMove += getWorld().getGravity();
        processMovement();

        super.moveRotation(0, rotationMove, 0);
        rotationMove *= 0.6;

        terrainHeight = getWorld().getTerrain().getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            super.getPosition().y = terrainHeight;
            upwardsMove = 0;
        }

    }

    private void processMovement() {
        if (getVelocity().length() > 0) {
            super.setCheckBounds(true);
            if (getWorld().findWorldIntersects(getBoundingBox()).size() > 0) {
                System.err.println("COLLISION "+getWorld().findWorldIntersects(getBoundingBox()).size());
            }
            super.movePosition(getVelocity().x, getVelocity().y, getVelocity().z);
            getVelocity().set(0, 0,0);
        }
    }
}
