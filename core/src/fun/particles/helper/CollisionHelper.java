package fun.particles.helper;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.List;

import fun.particles.base.Mass;
import fun.particles.custom.Particle;

public class CollisionHelper {

    private CollisionHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void velocityAfterCollision(Mass m1, Mass m2) {
        Vector2 t1 = m1.getVelocity().cpy().sub(m2.getVelocity());
        Vector2 t2 = m1.getCenter().cpy().sub(m2.getCenter());
        double massSum = (m1.getMass() + m2.getMass());
        double top = ((2 * m2.getMass()) / massSum) * t1.dot(t2);
        double bot = m1.getCenter().dst(m2.getCenter());
        float u = (float) (top / Math.pow(bot, 2));
        Vector2 m1v = m1.getVelocity().cpy().sub(t2.cpy().scl(u));
        t1 = t1.rotate(180);
        t2 = t2.rotate(180);
        top = ((2 * m1.getMass()) / massSum) * t1.dot(t2);
        u = (float) (top / Math.pow(bot, 2));
        Vector2 m2v = m2.getVelocity().cpy().sub(t2.cpy().scl(u));
        m2.setVelocity(m2v);
        m1.setVelocity(m1v);
    }

    public static void destroy(Mass[] toDestroy, Pool<Particle> particlePool, List<Particle> particles) {
        for (Mass m : toDestroy) {
            if (m != null) {
                m.destroyToParticles(particlePool, particles);
            }
        }
    }
}
