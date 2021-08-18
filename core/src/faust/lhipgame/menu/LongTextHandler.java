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
import faust.lhipgame.utils.TextLocalizer;

import java.util.Objects;

/**
 * Long text handler class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class LongTextHandler implements InputProcessor {

    private static final float FONT_SIZE = 0.5f;
    private static final float X_OFFSET = 5;
    private static final float Y_OFFSET = 10;
    private final TextLocalizer textLocalizer;

    private String longTextKey = "intro";
    private int longTextLastStep = 4;

    private BitmapFont mainFont;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private int currentIntroStep = 0;

    public LongTextHandler(TextLocalizer textLocalizer, String longTextKey, int longTextLastStep) {
        this.textLocalizer = textLocalizer;
        this.longTextKey = longTextKey;
        this.longTextLastStep = longTextLastStep;
    }

    public void loadFonts(AssetManager assetManager) {
        // Prepare font
        mainFont = assetManager.get("fonts/main_font.fnt");
        mainFont.getData().setScale(FONT_SIZE);
    }

    public void drawCurrentintro(SpriteBatch batch, OrthographicCamera camera) {
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
        mainFont.draw(batch, textLocalizer.localizeFromKey("cutscenes", longTextKey + ".text." + (currentIntroStep + 1)),
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
        return currentIntroStep >= longTextLastStep;
    }
}
