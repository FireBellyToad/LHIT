package com.faust.lhitgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.faust.lhitgame.camera.CameraManager;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.saves.AbstractSaveFileManager;
import com.faust.lhitgame.screens.LanguageScreen;
import com.faust.lhitgame.utils.TextLocalizer;

public class LHITGame extends Game {

    public static final int GAME_WIDTH = 160;
    public static final int GAME_HEIGHT = 144;

    private SpriteBatch batch;
    private AssetManager assetManager;
    private CameraManager cameraManager;
    private final AbstractSaveFileManager saveFileManager;
    private MusicManager musicManager;
    private TextLocalizer textLocalizer;

    public LHITGame(AbstractSaveFileManager saveFileManager) {
        super();
        this.saveFileManager = saveFileManager;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        cameraManager = new CameraManager();
        musicManager = new MusicManager();

        assetManager.load("fonts/main_font.fnt", BitmapFont.class);
        assetManager.finishLoading();

        textLocalizer = new TextLocalizer();

        setScreen(new LanguageScreen(this));
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

    public AbstractSaveFileManager getSaveFileManager() {
        return saveFileManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public TextLocalizer getTextLocalizer() {
        return textLocalizer;
    }
}
