package com.faust.lhengine.game.textbox;

import com.badlogic.gdx.Gdx;

/**
 * Class for wrapping text and duration
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class TextBoxData {

    private final String text;
    private final float timeToShow;
    private int charsToShow = 0;

    public TextBoxData(String text) {

        this.text = text;
        // 1 second plus 1 for each word (max 4)
        timeToShow = Math.min(4,1 + text.split(" ").length);

    }

    /**
     *
     * @return max time to show the text
     */
    public float getTimeToShow() {
        return timeToShow;
    }

    /**
     *
     * @return the whole text
     */
    public String getText() {

        return text;
    }

    /**
     *
     * @return a substring of the text, done one character per time to create a nice graphic effect
     */
    public String getTextIncremental(){
        String subText = text.substring(0, charsToShow);
        Gdx.app.log("DEBUG","TextBoxData::getTextIncremental -> charsToShow =  " + charsToShow );
        charsToShow = Math.min(text.length(), charsToShow+1);
        Gdx.app.log("DEBUG","TextBoxData::getTextIncremental -> charsToShow =  " + charsToShow );
        Gdx.app.log("DEBUG","TextBoxData::getTextIncremental -> this =  " + this );
        return subText;
    }

}
