package fun.particles.helper;

import com.badlogic.gdx.math.Vector2;

import fun.particles.base.Mass;

import static fun.particles.base.Const.energyLost;

public class CollisionHelper {
    static Vector2 helper = new Vector2(energyLost, energyLost);

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
        m1v.scl(1f - energyLost);
        m2v.scl(1f - energyLost);
        m2.setVelocity(m2v);
        m1.setVelocity(m1v);
        adjustCenter(m1, m2);

    }

    private static void adjustCenter(Mass m1, Mass m2) {
        float offset = m1.getCenter().dst(m2.getCenter());
        if (offset < 0.5f) {
            float v1l = m1.getVelocity().len();
            float v2l = m2.getVelocity().len();
            if (v1l < v2l) {
                m1.getCenter().add(m1.getVelocity().cpy().nor().scl(offset));
            } else {
                m2.getCenter().add(m2.getVelocity().cpy().nor().scl(offset));
            }
        }
    }
}
