package particles;

import entities.Point;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public class ParticleEmitter extends Point {

    private ParticleTexture texture;
    private int count;
    private float force;
    private float gravityFactor;
    private int lifeLength = 240;
    private Random random = new Random();

    private List<Particle> particles = new ArrayList<>();

    public ParticleEmitter(ParticleTexture texture, int count, float force, float gravityFactor) {
        this.texture = texture;
        this.count = count;
        this.force = force;
        this.gravityFactor = gravityFactor;
    }

    public void tick() {
        for (int i = 0; i < particles.size(); i++) {
            boolean stillAlive = particles.get(i).update();
            if (!stillAlive) {
                particles.remove(particles.get(i));
            }
        }
    }

    public void emitParticles() {
        for (int i = 0; i < count; i++){
            emitParticle(new Vector3f(getPosition()));
        }
    }

    private void emitParticle(Vector3f position) {
        float x = (random.nextFloat() - 0.5f) * force;
        float y = 1 + (random.nextFloat() - 0.5f) * force;
        float z = (random.nextFloat() - 0.5f) * force;
        particles.add(new Particle(texture, position, new Vector3f(x, y, z), gravityFactor, lifeLength, 0, 4, this.getWorld()));
    }

    public List<Particle> getParticles() {
        return particles;
    }
}
