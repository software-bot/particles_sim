package fun.particles;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import fun.particles.state.PlayState;

public class Application extends ApplicationAdapter {
    private PlayState playState;

    @Override
    public void create() {
        playState = new PlayState();
    }

    @Override
    public void render() {
        playState.draw();
    }

    @Override
    public void dispose() {
        playState.dispose();
    }
}
