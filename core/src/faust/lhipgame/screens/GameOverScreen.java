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
import faust.lhipgame.menu.enums.MenuItem;

/**
 * Class for game over screen
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class GameOverScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;
    private final Texture gameOverScreen;

    public GameOverScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();
        gameOverScreen = assetManager.get("splash/gameover_splash.png");

        menu = new Menu(game.getSaveFileManager(), MenuItem.GAME_OVER);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/gameover_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        //Loop title music
        musicManager.playMusic(TuneEnum.GAMEOVER, false);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToNextScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new MenuScreen(game));
        } else if (menu.isChangeToGameScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new GameScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(gameOverScreen, 0, 0);
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