package faust.lhipgame.text;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.LHIPGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Textboxes Manager
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class TextManager {

    private static final float FONT_SIZE = 0.5f;
    private static final float MIN_TIME_TO_SHOW = 2;
    private BitmapFont mainFont;
    private List<TextBoxData> textBoxes = new ArrayList<>();

    public TextManager() {

        mainFont = new BitmapFont(Gdx.files.internal("fonts/main_font.fnt"),
                Gdx.files.internal("fonts/main_font.png"), false);

        mainFont.getData().setScale(FONT_SIZE);
    }

    /**
     * Render a text box
     *
     * @param text
     */
    public void addNewTextBox(final String text) {
        Objects.requireNonNull(text);

        TextBoxData newText = new TextBoxData(text);
        textBoxes.add(newText);

        // Hide box after time
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                textBoxes.remove(newText);
            }
        }, newText.getTimeToShow());
    }

    public void renderTextBoxes(final SpriteBatch batch) {

        //Render all the created boxes
        textBoxes.forEach((box) -> {
            mainFont.draw(batch, box.getText(), 8, LHIPGame.GAME_HEIGHT - 8);
        });

    }

    public void dispose() {
        mainFont.dispose();
    }
}


//
//
//    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
//parameter.size = 18;
//        parameter.characters = "한국어/조선�?";
//
//        BitmapFont koreanFont = generator.generateFont(parameter);
//
//        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
//        generator = new FreeTypeFontGenerator(Gdx.files.internal("data/russkij.ttf"));
//        BitmapFont cyrillicFont = generator.generateFont(parameter);
//        generator.dispose();
