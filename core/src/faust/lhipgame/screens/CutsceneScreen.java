package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.GdxRuntimeException;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.utils.CutsceneEnum;
import faust.lhipgame.game.utils.TextLocalizer;
import faust.lhipgame.menu.LongTextHandler;

import java.lang.reflect.Constructor;
import java.util.Objects;

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
    private final TextLocalizer textLocalizer;

    private LongTextHandler longTextHandler;
    private Screen nextScreen;

    public CutsceneScreen(LHIPGame game, CutsceneEnum cutsceneEnum) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();

        musicManager.loadSingleTune(TuneEnum.DANGER, assetManager);

        //Init cutscene
        Objects.requireNonNull(cutsceneEnum);

        longTextHandler = new LongTextHandler(textLocalizer,cutsceneEnum.getKey(),cutsceneEnum.getStepsNumber());
        try {
            //Instantiate next screen using reflection
            Constructor ctor = cutsceneEnum.getNextScreenClass().getDeclaredConstructor(LHIPGame.class);
            this.nextScreen = (Screen) ctor.newInstance(game);
        } catch (Exception e) {
            throw new GdxRuntimeException(e);
        }

    }

    @Override
    public void show() {
        longTextHandler.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        //Loop title music
        musicManager.playMusic(TuneEnum.DANGER);

        Gdx.input.setInputProcessor(longTextHandler);
    }

    @Override
    public void render(float delta) {

        if (longTextHandler.isFinished()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(nextScreen);
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