package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.menu.Menu;

public class MenuScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private Menu menu;
    private Texture titleTexture;
    private Music titleMusic;

    public MenuScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        titleTexture = assetManager.get("splash/title_splash.png");
        titleMusic = assetManager.get("music/8-bit-chopin-funeral-march.ogg");

        menu = new Menu(game.getSaveFileManager());
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/loading_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        //Loop title music
        titleMusic.play();
        titleMusic.setLooping(true);
        titleMusic.setVolume(0.25f);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if(menu.isChangeToGameScreen()){
            //Stop music and change screen
            titleMusic.stop();
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