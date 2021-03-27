package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.menu.Menu;
import faust.lhipgame.menu.enums.MenuItem;

import java.util.Objects;

public class MenuScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private Menu menu;
    private Texture titleTexture;

    public MenuScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        titleTexture = assetManager.get("splash/title_splash.png");
        menu = new Menu(game.getSaveFileManager());
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/loading_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if(menu.isChangeToGameScreen()){
            game.setScreen(new LoadingScreen(game));
        } else{
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(titleTexture, 0, 0);
            menu.drawCurrentMenu(game.getBatch());
            game.getBatch().end();
        }

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