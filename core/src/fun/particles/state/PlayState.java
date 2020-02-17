package fun.particles.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.krystian.mass.ObjectFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import fun.particles.base.AbstractFactory;
import fun.particles.base.GravitationalObject;
import fun.particles.custom.Particle;
import fun.particles.custom.Sun;
import fun.particles.helper.CollisionHelper;

import static fun.particles.base.Const.SPEED_LIMITER;

public class PlayState implements GestureDetector.GestureListener {
    private List<GravitationalObject> bigObjects;
    private List<Particle> particles;
    private Pool<Particle> particlePool;
    private AbstractFactory<GravitationalObject> factory;
    private OrthographicCamera cam;
    private SpriteBatch spriteBatch;
    private Vector3 pointOnStage;
    private GravitationalObject[] toDestroy;
    private boolean isAiming;

    public PlayState() {
        cam = setupCamera();
        prepareLevel();
        fillInitialParticles();
    }

    private void prepareLevel() {
        factory = new ObjectFactory();
        spriteBatch = new SpriteBatch();
        bigObjects = new ArrayList<>();
        bigObjects.add(factory.create(Sun.class));
        particles = new ArrayList<>();
        toDestroy = new GravitationalObject[2];
        particlePool = new Pool<Particle>() {
            @Override
            protected Particle newObject() {
                return factory.create(Particle.class);
            }
        };
        prepareInputDetector();
    }

    private void prepareInputDetector() {
        GestureDetector gestureDetector = new GestureDetector(this);
        gestureDetector.setLongPressSeconds(0.8f);
        Gdx.input.setInputProcessor(gestureDetector);
    }

    private OrthographicCamera setupCamera() {
        int x = Gdx.graphics.getWidth();
        int y = Gdx.graphics.getHeight();
        float aspectRatio = (float) x / (float) y;
        float SIZE = 500;
        OrthographicCamera cam = new OrthographicCamera(SIZE * aspectRatio, SIZE);
        cam.translate(0, 0);
        cam.zoom = 3.5f;
        cam.update();
        return cam;
    }

    private void fillInitialParticles() {
        float scale = 0.9f;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int x = (int) (cam.position.x - (cam.viewportWidth * scale));
        int y = (int) (cam.position.y - (cam.viewportHeight * scale));
        int xTo = (int) (cam.position.x + (cam.viewportWidth * scale));
        int yTo = (int) (cam.position.y + (cam.viewportHeight * scale));
        int rotation = 90;

        for (int i = 0; i < 60000; i++) {
            int x1 = r.nextInt(x, xTo);
            int y1 = r.nextInt(y, yTo);
            Particle p = particlePool.obtain();
            p.setCenter(new Vector2(x1, y1));
//            float g = (float) Math.abs(r.nextGaussian() / 5);
//            if (g < 0.1f) {
//                g = 0.1f;
//            }
            p.setVelocity(p.getCenter().cpy().nor().scl(-0.1f));
            p.getVelocity().rotate(rotation);
            p.setRotation(r.nextFloat() * 5);
            particles.add(p);
        }
    }

    public void draw() {
        spriteBatch.setProjectionMatrix(cam.combined);
        spriteBatch.begin();
        render();
        spriteBatch.end();
    }

    private void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        updateAndDrawObjects();
    }

    private void updateAndDrawObjects() {
        float delta = Gdx.graphics.getDeltaTime() * 400;
        updateAndDrawParticles(delta);
        updateAndDrawBigObjects(delta);
    }

    private void updateAndDrawParticles(float delta) throws IllegalStateException {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            if (!p.isOutOfScreen(cam)) {
                p.update(delta);
                p.draw(spriteBatch);
                for (GravitationalObject g : bigObjects) {
                    p.updateVelocityByGravity(g, delta);
                    if (p.isMassIntersecting(g)) {
                        removeParticle(i, particles);
                    }
                }
            } else {
                removeParticle(i, particles);
            }
        }
    }

    private void removeParticle(int i, List<Particle> objects) {
        Particle removed = objects.remove(objects.size() - 1);
        if (i < objects.size() - 1)
            objects.set(i, removed);

        particlePool.free(removed);
    }

    private void updateAndDrawBigObjects(float delta) {
        for (int i = 0; i < bigObjects.size(); i++) {
            GravitationalObject m = bigObjects.get(i);
            if (!m.isOutOfScreen(cam)) {
                m.update(delta);
                m.draw(spriteBatch);
                for (int j = 0; j < bigObjects.size(); j++) {
                    if (i != j) {
                        GravitationalObject d = bigObjects.get(j);
                        m.updateVelocityByGravity(d, delta);
                        if (m.isMassIntersecting(d)) {
                            bigObjectsDestruction(m, d);
                        }
                    }
                }
            } else {
                bigObjects.remove(i);
                i--;
            }
        }
    }

    private void bigObjectsDestruction(GravitationalObject m, GravitationalObject d) {
        CollisionHelper.velocityAfterCollision(m, d);
        toDestroy[0] = m;
        toDestroy[1] = d;
        CollisionHelper.destroy(toDestroy, particlePool, particles);
        bigObjects.removeAll(Arrays.asList(toDestroy));
    }

    public void dispose() {
        spriteBatch.dispose();
        bigObjects.clear();
        particles.clear();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        System.exit(0);
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (!isAiming) {
            pointOnStage = new Vector3(x, y, 0);
            cam.unproject(pointOnStage);
            isAiming = true;
        }
        return true;
    }


    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        float x1 = pointOnStage.x, y1 = pointOnStage.y;
        Sun m = factory.create(Sun.class);
        m.setCenter(new Vector2(x1, y1));
        pointOnStage.x = x;
        pointOnStage.y = y;
        cam.unproject(pointOnStage);
        m.setVelocity(new Vector2((pointOnStage.x - x1) / SPEED_LIMITER, (pointOnStage.y - y1) / SPEED_LIMITER));
        bigObjects.add(m);
        isAiming = false;
        return true;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }
}
