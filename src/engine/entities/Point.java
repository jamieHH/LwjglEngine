package engine.entities;

import org.lwjgl.util.vector.Vector3f;
import engine.world.World;

public class Point {

    private World world;
    private Vector3f position;
    private Vector3f rotation = new Vector3f(0, 0, 0);
    private double radius = 0;

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void movePosition(float nx, float ny, float nz) {
        this.position.x += nx;
        this.position.y += ny;
        this.position.z += nz;
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

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void moveRotation(float nx, float ny, float nz) {
        this.rotation.x += nx;
        this.rotation.y += ny;
        this.rotation.z += nz;
    }

    public float getRotX() {
        return this.rotation.x;
    }

    public void setRotX(float rotX) {
        this.rotation.x = rotX;
    }

    public void moveRotX(float rotX) {
        this.rotation.x += rotX;
    }

    public float getRotY() {
        return rotation.y;
    }

    public void setRotY(float rotY) {
        this.rotation.y = rotY;
    }

    public void moveRotY(float rotY) {
        this.rotation.y += rotY;
    }

    public float getRotZ() {
        return rotation.z;
    }

    public void setRotZ(float rotZ) {
        this.rotation.z = rotZ;
    }

    public void moveRotZ(float rotZ) {
        this.rotation.z += rotZ;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    //---- Helpers
    public float distanceTo(Point p) {
        return (float) Math.sqrt(Math.pow(getPosX() - p.getPosX(), 2) + Math.pow(getPosY() - p.getPosY(), 2) + Math.pow(getPosZ() - p.getPosZ(), 2));
    }

    public float groundDistanceTo(float x, float z) {
        return (float) Math.hypot(Math.abs(getPosX() - x), Math.abs(getPosZ() - z));
    }

    public float squareDistanceTo(float x, float z) {
        return Math.max(Math.abs(this.getPosX() - x), Math.abs(this.getPosZ() - z));
    }

    public boolean groundContains(float x, float z) {
        if (this.getPosX() + radius <= x) return false;
        if (this.getPosX() - radius >= x) return false;
        if (this.getPosZ() + radius <= z) return false;
        if (this.getPosZ() - radius >= z) return false;
        return true;
    }

    public boolean isInside(float x0, float z0, float x1, float z1) {
        if (this.getPosX() + radius <= x0) return false;
        if (this.getPosX() - radius >= x1) return false;
        if (this.getPosZ() + radius <= z0) return false;
        if (this.getPosZ() - radius >= z1) return false;
        return true;
    }
}
