package faust.lhitgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhitgame.game.music.MusicManager;
import faust.lhitgame.utils.TextLocalizer;
import faust.lhitgame.saves.SaveFileManager;
import faust.lhitgame.camera.CameraManager;
import faust.lhitgame.screens.LanguageScreen;

public class LHITGame extends Game {

    public static final int GAME_WIDTH = 160;
    public static final int GAME_HEIGHT = 144;

    private SpriteBatch batch;
    private AssetManager assetManager;
    private CameraManager cameraManager;
    private SaveFileManager saveFileManager;
    private MusicManager musicManager;
    private TextLocalizer textLocalizer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        cameraManager = new CameraManager();
        saveFileManager = new SaveFileManager();
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

    public SaveFileManager getSaveFileManager() {
        return saveFileManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public TextLocalizer getTextLocalizer() {
        return textLocalizer;
    }
}
