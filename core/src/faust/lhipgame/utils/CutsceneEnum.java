package faust.lhipgame.utils;

import com.badlogic.gdx.Screen;
import faust.lhipgame.screens.EndGameScreen;
import faust.lhipgame.screens.LoadingScreen;

/**
 * Cutscenes Enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum CutsceneEnum {
    INTRO("intro", 4, LoadingScreen.class),
    ENDGAME("endgame", 4, EndGameScreen.class);

    private String key; // string key in messages.cutscenes
    private int stepsNumber; // number of steps of the cutscene
    private Class<? extends Screen> nextScreenClass; //Class of the next screen

    <T extends Screen>CutsceneEnum(String key, int stepsNumber, Class<T> nextScreenClass) {
        this.key = key;
        this.stepsNumber = stepsNumber;
        this.nextScreenClass = nextScreenClass;
    }

    public String getKey() {
        return key;
    }

    public int getStepsNumber() {
        return stepsNumber;
    }

    public Class<?> getNextScreenClass() {
        return nextScreenClass;
    }
}
