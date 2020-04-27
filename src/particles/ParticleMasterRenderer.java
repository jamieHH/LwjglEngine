package particles;

import entities.Point;
import org.lwjgl.util.vector.Matrix4f;

import java.util.*;

public class ParticleMasterRenderer {

    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(projectionMatrix);
    }

    public static void tick(Point camera) {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if (!stillAlive) {
                    iterator.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }

            // sort in order of distance to the camera
            list.sort((o1, o2) -> {
                if (o1.getDistanceToCamera() == o2.getDistanceToCamera()) {
                    return 0;
                }
                return o1.getDistanceToCamera() < o2.getDistanceToCamera() ? 1 : -1;
            });
        }
    }

    public static void renderParticles(Point camera) {
        renderer.render(particles, camera);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

    public static void addParticle(Particle particle) {
        List<Particle> list = particles.get(particle.getTexture());
        if (list == null) {
            list = new ArrayList<>();
            particles.put(particle.getTexture(), list);
        }
        list.add(particle);
    }
}
