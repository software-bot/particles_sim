package fun.particles.base;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class GravitationalObject extends Mass {
    private Sprite sprite;

    public GravitationalObject(Vector2 center, Vector2 velocity, float mass) {
        super(center, velocity, mass);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
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
}
