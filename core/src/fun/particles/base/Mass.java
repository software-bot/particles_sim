package fun.particles.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static fun.particles.base.Const.lost;

public abstract class Mass {

    private Vector2 center;
    private Vector2 velocity;
    private float radius, mass, radiusOffset;
    public int host;
    public int id;

    protected Mass(Vector2 center, Vector2 velocity, float mass) {
        this.center = center;
        this.velocity = velocity;
        this.mass = mass;
    }

    public void update(float dt) {
        center.add(velocity.cpy().scl(dt));
    }

    public boolean isMassIntersecting(Mass otherMass) {
        return host != otherMass.host && getCenter().dst(otherMass.getCenter()) <= getRadius() + otherMass.getRadius();
    }

    public abstract void draw(SpriteBatch spriteBatch);

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center;
        this.id = getId();
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

    public int getId() {
        return id((int) center.x, (int) center.y);
    }

    public static int id(int x, int y) {
        return ((x >> 3) * 100) + (y >> 2);
    }
}

