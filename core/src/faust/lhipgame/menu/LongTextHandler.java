package faust.lhipgame.menu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.utils.TextLocalizer;

/**
 * Long text handler class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class LongTextHandler  {

    private static final float FONT_SIZE = 0.5f;
    private static final float X_OFFSET = 5;
    private static final float Y_OFFSET = 10;
    private final TextLocalizer textLocalizer;

    private String longTextKey = "intro";

    private BitmapFont mainFont;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private int currentStep = 0;

    public LongTextHandler(TextLocalizer textLocalizer, String longTextKey) {
        this.textLocalizer = textLocalizer;
        this.longTextKey = longTextKey;
    }

    public void loadFonts(AssetManager assetManager) {
        // Prepare font
        mainFont = assetManager.get("fonts/main_font.fnt");
        mainFont.getData().setScale(FONT_SIZE);
    }

    public void drawCurrentintro(SpriteBatch batch, OrthographicCamera camera) {
        //TODO maybe should be nice to have fading text?
        mainFont.draw(batch, textLocalizer.localizeFromKey("cutscenes", longTextKey + ".text." + (currentStep + 1)),
                X_OFFSET, LHIPGame.GAME_HEIGHT - Y_OFFSET);
    }

    /**
     * Increments the step
     */
    public void goToNextStep() {
        currentStep++;
    }

    public int getCurrentStep() {
        return currentStep;
    }
}
