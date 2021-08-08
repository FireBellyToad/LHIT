package faust.lhipgame.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.textbox.manager.TextBoxManager;

import java.util.Objects;

/**
 * Menu class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Intro implements InputProcessor {

    private static final float FONT_SIZE = 0.5f;
    private static final float X_OFFSET = 5;
    private static final float Y_OFFSET = 10;
    private static final String INTRO_TEXT_KEY = "intro.text.";
    private static final int INTRO_LAST_STEP = 4;

    private BitmapFont mainFont;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private int currentIntroStep = 0;

    public Intro() {
    }

    public void loadFonts(AssetManager assetManager) {
        // Prepare font
        mainFont = assetManager.get("fonts/main_font.fnt");
        mainFont.getData().setScale(FONT_SIZE);
    }

    public void drawCurrentintro(SpriteBatch batch, OrthographicCamera camera, TextBoxManager textBoxManager) {
        Objects.requireNonNull(batch);

        //Left overflow
        batch.begin();
        backgroundBox.setColor(darkness);
        backgroundBox.setProjectionMatrix(camera.combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, 0, LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT);
        backgroundBox.end();
        batch.end();

        batch.begin();
        //TODO maybe should be nice to have fading text?
        mainFont.draw(batch, textBoxManager.localizeFromKey(INTRO_TEXT_KEY + (currentIntroStep + 1)),
                 X_OFFSET, LHIPGame.GAME_HEIGHT - Y_OFFSET);
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.X:
            case Input.Keys.K:
            case Input.Keys.ENTER: {
                currentIntroStep++;
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }

    public boolean isFinished() {
        return currentIntroStep >= INTRO_LAST_STEP;
    }
}
