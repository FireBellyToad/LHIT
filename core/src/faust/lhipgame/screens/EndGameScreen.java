package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.gameentities.SpriteEntity;
import faust.lhipgame.game.hud.enums.HudIconsEnum;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.menu.Menu;
import faust.lhipgame.menu.enums.MenuItem;
import faust.lhipgame.saves.SaveFileManager;

public class EndGameScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextBoxManager textBoxManager;
    private final SaveFileManager saveFileManager;
    private final Menu menu;
    private final Texture endGameScreen;
    private final SpriteEntity itemsTexture;

    public EndGameScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textBoxManager = game.getTextBoxManager();
        saveFileManager = game.getSaveFileManager();
        endGameScreen = assetManager.get("splash/gameover_splash.png");

        this.itemsTexture = new SpriteEntity(assetManager.get("sprites/hud.png")) {
            @Override
            protected int getTextureColumns() {
                return HudIconsEnum.values().length;
            }

            @Override
            protected int getTextureRows() {
                return 1;
            }
        };

        menu = new Menu(game.getSaveFileManager(), MenuItem.END_GAME);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/gameover_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        //Loop title music
        musicManager.playMusic(TuneEnum.ENDGAME, false);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToMainScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new MenuScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(endGameScreen, 0, 0);
            menu.drawCurrentMenuLocalized(game.getBatch(), textBoxManager);
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