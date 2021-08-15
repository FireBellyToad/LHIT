package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.utils.TextLocalizer;
import faust.lhipgame.menu.LongTextHandler;

/**
 * Intro screen class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CutsceneScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final LongTextHandler longTextHandler;
    private final TextLocalizer textLocalizer;

    public CutsceneScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();

        musicManager.loadSingleTune(TuneEnum.TITLE, assetManager);

        longTextHandler = new LongTextHandler(textLocalizer);
    }

    @Override
    public void show() {
        longTextHandler.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        //Loop title music
        musicManager.playMusic(TuneEnum.TITLE);

        Gdx.input.setInputProcessor(longTextHandler);
    }

    @Override
    public void render(float delta) {

        if (longTextHandler.isFinished()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new LoadingScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //intro screen render
            longTextHandler.drawCurrentintro(game.getBatch(), cameraManager.getCamera());
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