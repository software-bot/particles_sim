package fun.particles.custom;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import fun.particles.base.GravitationalObject;
import fun.particles.helper.CollisionHelper;
import fun.particles.state.PlayState;


public class Bag {
    private DList<GravitationalObject> particles;

    public Bag() {
        particles = new DList<>();
    }

    public void run(OrthographicCamera cam, float delta, SpriteBatch spriteBatch) {
        DList.Node<GravitationalObject> first = particles.first();
        while (first.val != null) {
            GravitationalObject p = first.val;
            p.update(delta);
            if (p.host > 1 && p.getVelocity().len() < 0.2f) {
                particles.remove(first);
                first = first.next;
                continue;
            }
            int newId = p.getId();
            if (newId != p.id) {
                particles.remove(first);
                p.id = newId;
                if (PlayState.bagMap.containsKey(newId))
                    PlayState.bagMap.get(newId).add(p);
                first = first.next;
                continue;
            }
            p.draw(spriteBatch);
            boundaryIntersection(first, cam);
            DList.Node<GravitationalObject> next = first.next;
            while (next.val != null) {
                if (p.isMassIntersecting(next.val)) {
                    CollisionHelper.velocityAfterCollision(p, next.val);
                    break;
                }
                next = next.next;
            }
            first = first.next;
        }
    }

    public void add(GravitationalObject p) {
        particles.add(p);
    }

    private void boundaryIntersection(DList.Node<GravitationalObject> n, OrthographicCamera cam) {
        GravitationalObject p = n.val;
        if (p.isOutOfScreen(cam)) {
            particles.remove(n);
        }
//        if (p.getCenter().x < -120 || p.getCenter().x > 120) {
//            p.getVelocity().setAngle(180 - p.getVelocity().angle());
//            p.getVelocity().scl(vLost);
//            particles.remove(n);
//        } else if (p.getCenter().y < -70 || p.getCenter().y > 70) {
//            p.getVelocity().setAngle(360 - p.getVelocity().angle());
//            p.getVelocity().scl(vLost);
//            particles.remove(n);
//        }
    }
}
