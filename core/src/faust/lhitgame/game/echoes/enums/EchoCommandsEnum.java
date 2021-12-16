package faust.lhitgame.game.echoes.enums;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Valid Echo commands enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum EchoCommandsEnum {
    //Simple
    TEXTBOX_KEY("textBoxKey", String.class),
    RENDER_ONLY_MAP_LAYER("renderOnlyMapLayer", String.class),
    DIRECTION("direction", String.class, true),
    SPEED("speed", Integer.class, true),
    STEP("step", Integer.class, true),
    TIMES("times", Integer.class),
    UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE("untilAtLeastOneKillableAlive", String.class),
    UNTIL_AT_LEAST_ONE_POI_EXAMINABLE("untilAtLeastOnePOIExaminable", Integer.class),
    UNTIL_AT_LEAST_ONE_PLAYER_DAMAGE_IS_LESS_THAN("untilAtLeastPlayerDamageIsLessThan", Integer.class),
    SPLASH_TO_SHOW("splashToShow", String.class),
    INSTANCE("instance", String.class, true),
    X("instance", Integer.class),
    Y("instance", Integer.class),

    //Composite
    MOVE("move", EchoCommandsEnum.class, new EchoCommandsEnum[]{DIRECTION, SPEED}),
    GO_TO("goTo", EchoCommandsEnum.class, new EchoCommandsEnum[]{STEP, TIMES, UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE, UNTIL_AT_LEAST_ONE_POI_EXAMINABLE, UNTIL_AT_LEAST_ONE_PLAYER_DAMAGE_IS_LESS_THAN}),
    SPAWN("move", EchoCommandsEnum.class, new EchoCommandsEnum[]{INSTANCE, X, Y});

    private String commandString;
    private Class<?> valueClass;
    private EchoCommandsEnum[] subCommands;
    private boolean isRequired;


    EchoCommandsEnum(String commandString, Class<?> valueClass) {
        this.commandString = commandString;
        this.valueClass = valueClass;
        this.isRequired = false;
        this.subCommands = null;
    }

    EchoCommandsEnum(String commandString, Class<?> valueClass, boolean isRequired) {
        this.commandString = commandString;
        this.valueClass = valueClass;
        this.isRequired = isRequired;
        this.subCommands = null;
    }

    EchoCommandsEnum(String commandString, Class<?> valueClass, EchoCommandsEnum[] subCommands) {
        this.commandString = commandString;
        this.valueClass = valueClass;
        this.isRequired = false;
        this.subCommands = subCommands;
    }

    public String getCommandString() {
        return commandString;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public EchoCommandsEnum[] getSubCommands() {
        return subCommands;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public static EchoCommandsEnum getFromCommandString(String command) {
        for (EchoCommandsEnum e : EchoCommandsEnum.values()) {
            if (e.getCommandString().equals(command)) {
                return e;
            }
        }
        return null;
    }
}

