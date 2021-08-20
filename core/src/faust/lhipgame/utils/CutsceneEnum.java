package faust.lhipgame.utils;

import com.badlogic.gdx.Screen;
import faust.lhipgame.screens.EndGameScreen;
import faust.lhipgame.screens.GameScreen;

/**
 * Cutscenes Enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum CutsceneEnum {
    INTRO("intro", 4, GameScreen.class),
    ENDGAME("endgame", 4, EndGameScreen.class);

    private final String key; // string key in messages.cutscenes
    private final int stepsNumber; // number of steps of the cutscene
    private final Class<? extends Screen> nextScreenClass; //Class of the next screen

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
