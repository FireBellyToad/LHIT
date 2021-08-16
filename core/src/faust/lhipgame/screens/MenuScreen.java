package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.utils.TextLocalizer;
import faust.lhipgame.menu.Menu;

/**
 * Menu screen class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MenuScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;
    private final Texture titleTexture;

    public MenuScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();

        titleTexture = assetManager.get("splash/title_splash.png");
        musicManager.loadSingleTune(TuneEnum.TITLE, assetManager);

        menu = new Menu(game.getSaveFileManager(),assetManager);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/loading_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        //Loop title music
        musicManager.playMusic(TuneEnum.TITLE);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToIntroScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            CutsceneScreen screen = new CutsceneScreen(game);
            screen.initCutscene("intro",4, new LoadingScreen(game));
            game.setScreen(screen);
        } else if (menu.isChangeToGameScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new LoadingScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(titleTexture, 0, 0);
            menu.drawCurrentMenuLocalized(game.getBatch(), textLocalizer);
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