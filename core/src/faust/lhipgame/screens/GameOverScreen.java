package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.menu.Menu;
import faust.lhipgame.menu.enums.MenuItem;

public class GameOverScreen  implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final Menu menu;
    private final Texture gameOverScreen;
    private final Music gameOverMusic;

    public GameOverScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        gameOverScreen = assetManager.get("splash/gameover_splash.png");
        gameOverMusic = assetManager.get("music/8-bit-chopin-funeral-march.ogg");

        menu = new Menu(game.getSaveFileManager(), MenuItem.GAME_OVER);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/gameover_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        //Loop title music
        gameOverMusic.play();
        gameOverMusic.setLooping(true);
        gameOverMusic.setVolume(0.25f);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if(menu.isChangeToMainScreen()){
            //Stop music and change screen
            gameOverMusic.stop();
            game.setScreen(new MenuScreen(game));
        } else if(menu.isChangeToGameScreen()){
            //Stop music and change screen
            gameOverMusic.stop();
            game.setScreen(new GameScreen(game));
        } else{
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(gameOverScreen, 0, 0);
            menu.drawCurrentMenu(game.getBatch());
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