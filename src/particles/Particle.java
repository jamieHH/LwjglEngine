package particles;


import org.lwjgl.util.vector.Vector3f;
import world.World;

public class Particle {

    private Vector3f position;
    private World world;
    private Vector3f velocity;
    private float gravityFactor;
    private float lifeLength;
    private float rotation;
    private float scale;

    private ParticleTexture texture;

    private float elapsedTime;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityFactor, float lifeLength, float rotation, float scale, World world) {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityFactor = gravityFactor;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;

        this.world = world;

        ParticleMasterRenderer.addParticle(this);
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
        velocity.y += world.getGravity() * gravityFactor; // + world gravity
        Vector3f change = new Vector3f(velocity);
        change.scale(1f);
        Vector3f.add(change, position, position);
        elapsedTime += 1;
        return elapsedTime < lifeLength;
    }

    public World getWorld() {
        return world;
    }
}
