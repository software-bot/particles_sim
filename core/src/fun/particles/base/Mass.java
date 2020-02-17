package fun.particles.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.List;

import fun.particles.custom.Particle;

import static fun.particles.base.Const.G;

public abstract class Mass {

    private Vector2 center;
    private Vector2 velocity;
    private float radius;
    private float mass;
    private float force;
    private float radiusOffset;

    protected Mass(Vector2 center, Vector2 velocity, float mass) {
        this.center = center;
        this.velocity = velocity;
        this.mass = mass;
        this.force = mass * G;
    }

    public void update(float dt) {
        center.add(velocity.cpy().scl(dt));
    }

    public boolean isMassIntersecting(Mass otherMass) {
        return getCenter().dst(otherMass.getCenter()) + radiusOffset + otherMass.radiusOffset <= getRadius() + otherMass.getRadius();
    }

    public abstract void draw(SpriteBatch spriteBatch);

    abstract public void destroyToParticles(Pool<Particle> particlePool, List<Particle> particles);

    abstract public void updateVelocityByGravity(Mass mass, float delta);

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setRadiusOffset(float radiusOffset) {
        this.radiusOffset = radiusOffset;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.x = x;
        this.velocity.y = y;
    }

    public void setCenter(float x, float y) {
        this.center.x = x;
        this.center.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public float getRadiusOffset() {
        return radiusOffset;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getForce() {
        return force;
    }
}

