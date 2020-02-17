package fun.particles.base;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.List;

import fun.particles.custom.Particle;

public abstract class GravitationalObject extends Mass {
    private Sprite sprite;
    private float rotation;

    public GravitationalObject(Vector2 center, Vector2 velocity, float mass) {
        super(center, velocity, mass);
    }

    @Override
    public void updateVelocityByGravity(Mass mass, float delta) {
        getVelocity().add(getGravityForce(mass, delta));
    }

    private Vector2 getGravityForce(Mass mass, float delta) {
        return mass.getCenter()
                .cpy()
                .sub(this.getCenter())
                .nor().scl(mass.getForce() * delta / this.getCenter().dst2(mass.getCenter()));
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        if (rotation != 0)
            sprite.rotate(rotation);
        sprite.draw(spriteBatch);
    }

    public boolean isOutOfScreen(Camera camera) {
        return !camera.frustum.pointInFrustum(getCenter().x, getCenter().y, 0);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        sprite.setPosition(getCenter().x - getRadius(),
                getCenter().y - getRadius());
    }

    public abstract String texturePath();

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        setRadius(sprite.getWidth() / 2);
    }

    @Override
    public void destroyToParticles(Pool<Particle> particlePool, List<Particle> particles) {
        Vector2 scalar = getVelocity().cpy();
        Vector2 centerOffset = new Vector2((getRadius() - getRadiusOffset()) / 2, (getRadius() - getRadiusOffset()) / 2);
        int pAmount = 2000;
        for (int i = 0; i < pAmount; i++) {
            centerOffset.rotate(i);
            particles.add(createParticle(particlePool, scalar, i, centerOffset));
        }
    }

    private Particle createParticle(Pool<Particle> particlePool, Vector2 scalar, int i, Vector2 centerOffset) {
        Particle particle = particlePool.obtain();
        particle.setCenter(getCenter().x + (centerOffset.x), getCenter().y + (centerOffset.y));
        particle.setVelocity(getVelocity().x, getVelocity().y);
        float gaussian = Const.gaussian[i];
        particle.setRotation(gaussian);
        particle.getVelocity().add(scalar.cpy().rotate(i).scl(gaussian));
        return particle;
    }
}
