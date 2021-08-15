package faust.lhipgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.game.utils.TextLocalizer;
import faust.lhipgame.saves.SaveFileManager;
import faust.lhipgame.screens.CameraManager;
import faust.lhipgame.screens.FBTScreen;
import faust.lhipgame.screens.LanguageScreen;

public class LHIPGame extends Game {

    public static final int GAME_WIDTH = 160;
    public static final int GAME_HEIGHT = 144;

    private String language = "eng";
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
