package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;

public class Player extends Entity {

    private static final float RUN_SPEED = 0.5f;
    private static final float TURN_SPEED = 4.5f;

    private static final float GRAVITY = -0.025f;
    private static final float JUMP_POWER = 1f;
    private float terrainHeight = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    public Player(TexturedModel model, float scale) {
        super(model, scale);
    }

    private boolean getIsFloating() {
        return super.getPosition().y > terrainHeight;
    }

    public void tick() {
        checkInputs();
        super.moveRotation(0, currentTurnSpeed, 0);
        float dx = (float) (currentSpeed * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (currentSpeed * Math.cos(Math.toRadians(super.getRotY())));
        super.movePosition(dx, 0, dz);

        upwardsSpeed += GRAVITY;
        super.movePosition(0, upwardsSpeed, 0);
        terrainHeight = getWorld().getTerrain().getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            super.getPosition().y = terrainHeight;
            upwardsSpeed = 0;
        }
    }

    private void jump() {
        if (!getIsFloating()) {
            this.upwardsSpeed = JUMP_POWER;
        }
    }

    private void checkInputs() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            this.currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            this.jump();
        }
    }
}
