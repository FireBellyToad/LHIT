package faust.lhitgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import faust.lhitgame.LHITGame;
import faust.lhitgame.cutscenes.CutsceneManager;
import faust.lhitgame.game.music.MusicManager;
import faust.lhitgame.game.music.enums.TuneEnum;
import faust.lhitgame.utils.CutsceneEnum;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * Intro screen class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CutsceneScreen implements Screen {

    private static final Color back = new Color(0x000000ff);

    private final LHITGame game;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final ShapeRenderer background;

    private final CutsceneManager cutsceneManager;
    private final Screen nextScreen;

    private float stateTime = 0f;

    public CutsceneScreen(LHITGame game, CutsceneEnum cutsceneEnum) {
        this.game = game;
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();

        musicManager.loadSingleTune(TuneEnum.DANGER, game.getAssetManager());
        background = new ShapeRenderer();

        //Init cutscene
        Objects.requireNonNull(cutsceneEnum);

        cutsceneManager = new CutsceneManager(cutsceneEnum, game.getAssetManager(), game.getTextLocalizer(),cameraManager.getCamera(),  game.getSaveFileManager());

        try {
            //Instantiate next screen using reflection
            Constructor<?> ctor = cutsceneEnum.getNextScreenClass().getDeclaredConstructor(LHITGame.class);
            this.nextScreen = (Screen) ctor.newInstance(game);
        } catch (Exception e) {
            throw new GdxRuntimeException(e);
        }

    }

    @Override
    public void show() {
        cutsceneManager.initCutscene();

        //Loop title music
        musicManager.playMusic(TuneEnum.DANGER);

        Gdx.input.setInputProcessor(cutsceneManager);
    }

    @Override
    public void render(float delta) {

        if (cutsceneManager.isFinished()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(nextScreen);
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            stateTime += Gdx.graphics.getDeltaTime();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            renderBlackBackground();

            //intro screen render
            cutsceneManager.draw(game.getBatch(), stateTime, cameraManager.getCamera());
        }

    }

    private void renderBlackBackground() {

        game.getBatch().begin();
        background.setColor(back);
        background.setProjectionMatrix(cameraManager.getCamera().combined);
        background.begin(ShapeRenderer.ShapeType.Filled);
        background.rect(0, 0, LHITGame.GAME_WIDTH, LHITGame.GAME_HEIGHT);
        background.end();
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