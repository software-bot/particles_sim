package fun.particles.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;
import java.util.Map;

import fun.particles.base.AbstractFactory;
import fun.particles.base.GravitationalObject;
import fun.particles.base.Mass;
import fun.particles.custom.Bag;
import fun.particles.custom.Particle;
import fun.particles.factory.ObjectFactory;

import static fun.particles.base.Const.SPEED_LIMITER;

public class PlayState implements GestureDetector.GestureListener {
    public static Map<Integer, Bag> bagMap;
    private Pool<Particle> particlePool;
    private AbstractFactory<GravitationalObject> factory;
    private OrthographicCamera cam;
    private SpriteBatch spriteBatch;
    private Vector3 pointOnStage;
    private int host = 1;
    private boolean isAiming;

    public PlayState() {
        cam = setupCamera();
        prepareLevel();
        for (int i = -130; i < 130; i++) {
            for (int j = -50; j < 50; j++)
                bagMap.put(Mass.id(i, j), new Bag());
        }
        fillInitialParticles();
    }

    private void prepareLevel() {
        factory = new ObjectFactory();
        spriteBatch = new SpriteBatch();
        bagMap = new HashMap<>();
        particlePool = new Pool<Particle>() {
            @Override
            protected Particle newObject() {
                return factory.create(Particle.class);
            }
        };
        prepareInputDetector();
    }

    private void fillInitialParticles() {
        float scale = 0.4f;
        int x = (int) (cam.position.x - (cam.viewportWidth * scale));
        int y = (int) (cam.position.y - (cam.viewportHeight * scale));
        int xTo = (int) (cam.position.x + (cam.viewportWidth * scale));
        int yTo = (int) (cam.position.y + (cam.viewportHeight * scale));
        int h = -1;
        for (float i = x; i < xTo; i += 1) {
            for (float j = y; j < yTo; j += 1) {
                Particle p = particlePool.obtain();
                p.setCenter(new Vector2(i, j));
                p.host = h--;
                bagMap.putIfAbsent(p.id, new Bag());
                bagMap.get(p.getId()).add(p);
            }
        }
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
        float SIZE = 100;
        OrthographicCamera cam = new OrthographicCamera(SIZE * aspectRatio, SIZE);
        cam.translate(0, 0);
        cam.zoom = 1.5f;
        cam.update();
        return cam;
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
        float delta = Gdx.graphics.getDeltaTime() * 100;
        for (Integer key : bagMap.keySet()) {
            bagMap.get(key).run(cam, delta, spriteBatch);
        }
    }

    public void dispose() {
        spriteBatch.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Particle.mass = 300;
        Vector3 vector3 = new Vector3(x, y, 0);
        vector3 = cam.unproject(vector3);
        int radius = 0;
        for (float i = 0; i < 360; i += 1) {
            Particle p = particlePool.obtain();
            p.setCenter(new Vector2(vector3.x + ((float) Math.cos(Math.toRadians(i)) * radius),
                    vector3.y + ((float) Math.sin(Math.toRadians(i)) * radius)));
            p.setVelocity(new Vector2((float) Math.cos(Math.toRadians(i)), (float) Math.sin(Math.toRadians(i))));
            p.getVelocity().scl(0.5f);
            p.host = host;
            bagMap.putIfAbsent(p.id, new Bag());
            bagMap.get(p.id).add(p);
        }
        host++;
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
        return false;
    }


    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Particle.mass = 3000000;
        float x1 = pointOnStage.x, y1 = pointOnStage.y;
        pointOnStage.x = x;
        pointOnStage.y = y;
        cam.unproject(pointOnStage);
        float radius = 0.5f;
        for (float i = 0; i < 360; i += 2) {
            Particle p = particlePool.obtain();
            p.setCenter(new Vector2(x1 + ((float) Math.cos(Math.toRadians(i)) * radius),
                    y1 + ((float) Math.sin(Math.toRadians(i)) * radius)));
            p.setVelocity(new Vector2((pointOnStage.x - x1) / SPEED_LIMITER, (pointOnStage.y - y1) / SPEED_LIMITER));
            p.getVelocity().scl(0.5f);
            p.host = host;
            bagMap.putIfAbsent(p.id, new Bag());
            bagMap.get(p.id).add(p);
        }
        host++;
        isAiming = false;
        return false;
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
