package faust.lhipgame.text;

/**
 * Class for wrapping text and duration
 */
public class TextBoxData {

    private String text;
    private float timeToShow;

    public TextBoxData(String text) {

        this.text = text;
        // 1 second plus 1 for each word
        timeToShow = 1 + text.split(" ").length;

    }

    public void setTimeToShow(float timeToShow) {
        this.timeToShow = timeToShow;
    }

    public float getTimeToShow() {
        return timeToShow;
    }

    public String getText() {

        return text;
    }

}
