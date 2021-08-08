package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.menu.Intro;

/**
 * Intro screen class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class IntroScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextBoxManager textBoxManager;
    private final Intro intro;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    public IntroScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textBoxManager = game.getTextBoxManager();

        musicManager.loadSingleTune(TuneEnum.TITLE, assetManager);

        intro = new Intro();
    }

    @Override
    public void show() {
        intro.loadFonts(assetManager);

        //Loop title music
        musicManager.playMusic(TuneEnum.TITLE);

        Gdx.input.setInputProcessor(intro);
    }

    @Override
    public void render(float delta) {

        if (intro.isFinished()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new LoadingScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //intro screen render
            intro.drawCurrentintro(game.getBatch(), cameraManager.getCamera(), textBoxManager);
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