package fun.particles.custom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import fun.particles.base.GravitationalObject;

public class Particle extends GravitationalObject {
    private Color color;

    public Particle() {
        this(new Vector2(0, 0), new Vector2(0, 0), 1);
    }

    private Particle(Vector2 center, Vector2 velocity, float mass) {
        super(center, velocity, mass);
        color = new Color(1, 1, 1, 1);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        changeColorBasedOnVelocity();
        getSprite().setColor(color);
        super.draw(spriteBatch);
    }

    @Override
    public String texturePath() {
        return "dot.png";
    }


    private void changeColorBasedOnVelocity() {
        float speed = getVelocity().len();
        color.b = colorValReverse(0.8f - speed);
        color.g = colorVal(speed);
        color.r = colorVal((speed * 2));
    }

    private float colorVal(float val) {
        if (val > 1f) {
            val = 1f;
        }
        return val;
    }

    private float colorValReverse(float val) {
        if (val < 0f) {
            val = 0f;
        }
        return val;
    }
}
