package entities;

import org.lwjgl.util.vector.Vector3f;
import world.World;

public class Point {

    private World world;
    private Vector3f position;
    private float rotX, rotY, rotZ;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void movePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public float getPosX() {
        return this.position.x;
    }

    public void setPosX(float posX) {
        this.position.x = posX;
    }

    public void movePosX(float moveX) {
        this.position.x += moveX;
    }

    public float getPosY() {
        return this.position.y;
    }

    public void setPosY(float posY) {
        this.position.y = posY;
    }

    public void movePosY(float moveY) {
        this.position.y += moveY;
    }

    public float getPosZ() {
        return this.position.z;
    }

    public void setPosZ(float posZ) {
        this.position.z = posZ;
    }

    public void movePosZ(float moveZ) {
        this.position.z += moveZ;
    }

    public void setRotation(float rotX, float rotY, float rotZ) {
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public void moveRotation(float rotX, float rotY, float rotZ) {
        this.rotX += rotX;
        this.rotY += rotY;
        this.rotZ += rotZ;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public void moveRotX(float rotX) {
        this.rotX += rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public void moveRotY(float rotY) {
        this.rotY += rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public void moveRotZ(float rotZ) {
        this.rotZ += rotZ;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
