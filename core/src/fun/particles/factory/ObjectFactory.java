package fun.particles.factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fun.particles.base.AbstractFactory;
import fun.particles.base.GravitationalObject;
import fun.particles.custom.Particle;
import fun.particles.custom.Rock;

public class ObjectFactory implements AbstractFactory<GravitationalObject> {
    private Map<Class<? extends GravitationalObject>, Texture> textureMap;

    public ObjectFactory() {
        initTextures();
    }

    private void initTextures() {
        textureMap = new HashMap<>();
        List<GravitationalObject> loader = new ArrayList<>();
        loader.add(new Particle());
        loader.add(new Rock());
        for (GravitationalObject implClass : loader) {
            if (implClass.texturePath() != null)
                textureMap.put(implClass.getClass(), new Texture(implClass.texturePath()));
        }
    }

    @Override
    public <T extends GravitationalObject> T create(Class<T> name) {
        try {
            T instance = name.getConstructor().newInstance();
            Texture texture = textureMap.get(name);
            if (texture != null) {
                instance.setSprite(new Sprite(texture));
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
