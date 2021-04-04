package faust.lhipgame.game.textbox;

/**
 * Class for wrapping text and duration
 */
public class TextBoxData {

    private final String text;
    private final float timeToShow;

    public TextBoxData(String text) {

        this.text = text;
        // 1 second plus 1 for each word (max 4)
        timeToShow = Math.min(4,1 + text.split(" ").length);

    }

    public float getTimeToShow() {
        return timeToShow;
    }

    public String getText() {

        return text;
    }

}
