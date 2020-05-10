package faust.lhipgame.text.manager;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.text.TextBoxData;

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
    private static final float MESSAGE_LIMIT = 1;

    private BitmapFont mainFont;
    private List<TextBoxData> textBoxes = new ArrayList<>();
    private JsonValue messageMap;

    private ShapeRenderer backgroundBox;
    private ShapeRenderer cornerBox;
    private static final Color corner = new Color(0xffffffff);
    private static final Color back = new Color(0x222222ff);

    public TextManager() {

        // Prepare font
        mainFont = new BitmapFont(Gdx.files.internal("fonts/main_font.fnt"),
                Gdx.files.internal("fonts/main_font.png"), false);

        mainFont.getData().setScale(FONT_SIZE);

        // Prepare text map
        JsonValue root = new JsonReader().parse(Gdx.files.internal("messages/textBoxes.json"));
        messageMap = root.get("messages");

        Objects.requireNonNull(messageMap);

        backgroundBox = new ShapeRenderer();
        cornerBox = new ShapeRenderer();
    }

    /**
     * Generate a new text box
     *
     * @param textKey the key of the message to be shown
     */
    public void addNewTextBox(final String textKey) {
        Objects.requireNonNull(textKey);

        if (textBoxes.size() == MESSAGE_LIMIT) {
            textBoxes.remove(0);
        }

        //Create text box given a textKey. If no text is found, use key as text
        TextBoxData newText = new TextBoxData(messageMap.getString(textKey, textKey));
        textBoxes.add(newText);

        // Hide box after time
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                textBoxes.remove(newText);
            }
        }, newText.getTimeToShow());
    }

    /**
     * Render all the generated (right now just one) text boxes
     *  @param batch
     * @param player
     * @param camera
     */
    public void renderTextBoxes(final SpriteBatch batch, PlayerInstance player, OrthographicCamera camera) {

        if(player.getBody().getPosition().y <= 48){
            textBoxes.clear();
            return;
        }

        //Render all the created boxes
        textBoxes.forEach((box) -> {

            //White Corner
            batch.begin();
            cornerBox.setColor(corner);
            cornerBox.setProjectionMatrix(camera.combined);
            cornerBox.begin(ShapeRenderer.ShapeType.Filled);
            cornerBox.rect(0, 0, LHIPGame.GAME_WIDTH, 48);
            cornerBox.end();

            //Black Background
            backgroundBox.setColor(back);
            backgroundBox.setProjectionMatrix(camera.combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(4, 4, LHIPGame.GAME_WIDTH-8, 40);
            backgroundBox.end();
            batch.end();

            //Text
            batch.begin();
            mainFont.draw(batch, box.getText(), 8, 40);
            batch.end();
        });
    }

    public void dispose() {
        mainFont.dispose();
    }
}

