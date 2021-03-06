package engine.entities;


import engine.particles.ParticleTexture;
import org.lwjgl.util.vector.Vector3f;
import engine.world.World;

public class Particle {

    private ParticleTexture texture;
    private Vector3f position;
    private World world;
    private Vector3f velocity;
    private float gravityFactor;
    private float rotation;
    private float scale;

    private int elapsedTime;
    private int lifeLength;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityFactor, int lifeLength, float rotation, float scale, World world) {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityFactor = gravityFactor;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        this.world = world;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    protected boolean update() {
        elapsedTime++;
        velocity.y += world.getGravity() * gravityFactor; // + engine.world gravity
        Vector3f.add(velocity, position, position);
        return elapsedTime < lifeLength;
    }

    public World getWorld() {
        return world;
    }

    public float distanceTo(Point point) {
        return Vector3f.sub(point.getPosition(), position, null).lengthSquared();
    }
}
