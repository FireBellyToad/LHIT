package faust.lhipgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhipgame.screens.CameraManager;
import faust.lhipgame.screens.GameScreen;
import faust.lhipgame.screens.LoadingScreen;

public class LHIPGame extends Game {

    public static final int GAME_WIDTH = 160;
    public static final int GAME_HEIGHT = 144;

    private SpriteBatch batch;
    private AssetManager assetManager;
    private CameraManager cameraManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        cameraManager = new CameraManager();

        assetManager.load("splash/loading_splash.png", Texture.class);
        assetManager.finishLoading();

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void dispose() {

        getScreen().dispose();
        assetManager.dispose();
        cameraManager.dispose();
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
}
