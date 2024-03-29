package com.faust.lhitgame.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.camera.CameraManager;

public class FBTScreen implements Screen {

    private final LHITGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final Texture fbtScreen;

    public FBTScreen(LHITGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        fbtScreen = assetManager.get("splash/fbt_splash.png");
    }

    @Override
    public void show() {

        // Load next menu assets
        assetManager.load("splash/title_splash.png", Texture.class);
        assetManager.finishLoading();

        assetManager.load("fonts/main_font.png", Texture.class);
        assetManager.finishLoading();

        //Load next screen image
        assetManager.load("splash/loading_splash.png", Texture.class);
        assetManager.finishLoading();

        //Two seconds splash screen
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new LoadingScreen(game));
            }
        }, 2);
    }

    @Override
    public void render(float delta) {

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);
        cameraManager.renderBackground();

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
