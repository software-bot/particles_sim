package fun.particles.custom;

import com.badlogic.gdx.math.Vector2;

import fun.particles.base.GravitationalObject;


public class Sun extends GravitationalObject {
    private final static float MASS = 498900000000f;

    public Sun() {
        super(new Vector2(0, 0), new Vector2(0, 0), MASS);
        setRotation(0.1f);
        setRadiusOffset(115);
    }

    @Override
    public String texturePath() {
        return "sun.png";
    }
}
