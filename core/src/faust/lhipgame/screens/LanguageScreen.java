package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.utils.TextLocalizer;
import faust.lhipgame.menu.Menu;
import faust.lhipgame.menu.enums.MenuItem;

/**
 * Class for game over screen
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class LanguageScreen implements Screen {

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;

    public LanguageScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        textLocalizer = game.getTextLocalizer();

        menu = new Menu(game.getSaveFileManager(), MenuItem.LANGUAGE, assetManager);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/fbt_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToNextScreen()) {
            //Set language and Change screen
            textLocalizer.setLanguage( menu.getSelectedMenuVoice() == 0 ? "eng" : "ita");
            game.setScreen(new FBTScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Black background
            game.getBatch().begin();
            backgroundBox.setColor(darkness);
            backgroundBox.setProjectionMatrix(cameraManager.getCamera().combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(0, 0, LHIPGame.GAME_WIDTH,  LHIPGame.GAME_HEIGHT);
            backgroundBox.end();
            game.getBatch().end();

            //Menu screen render
            game.getBatch().begin();
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