package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.LHIPGame;

public class FBTScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private Texture fbtScreen;

    public FBTScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        fbtScreen = assetManager.get("splash/fbt_splash.png");
    }

    @Override
    public void show() {

        // Load next screen splash
        assetManager.load("splash/loading_splash.png", Texture.class);

        assetManager.finishLoading();
    }

    @Override
    public void render(float delta) {

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);
        cameraManager.renderBackground();

        //Three seconds splash screen
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new LoadingScreen(game));
            }
        }, 3);

        //Load screen
        game.getBatch().begin();
        game.getBatch().draw(fbtScreen,0,0);
        game.getBatch().end();

    }

    @Override
    public void resize(int width, int height) {
        cameraManager.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
