package com.faust.lhitgame.game.echoes.enums;

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
    IF_AT_LEAST_ONE_KILLABLE_ALIVE("ifAtLeastOneKillableAlive", String.class),
    IF_AT_LEAST_ONE_POI_EXAMINABLE("ifAtLeastOnePOIExaminable", String.class),
    IF_PLAYER_DAMAGE_IS_LESS_THAN("ifPlayerDamageIsLessThan", Integer.class),
    SPLASH_TO_SHOW("splashToShow", String.class),
    INSTANCE_CLASS("instanceClass", String.class, true),
    IDENTIFIER("identifier", String.class, true),
    X("x", Integer.class),
    Y("y", Integer.class),
    RELATIVE("relative", Boolean.class),
    INVISIBLE("invisible", Boolean.class),
    DAMAGE("damage",Integer.class,true),
    CAN_KILL_PLAYER("canKillPlayer",Boolean.class),
    IF_NO_KILLABLE_ALIVE("ifNoKillableAlive", String.class),
    IF_PLAYER_DAMAGE_IS_MORE_THAN("ifPlayerDamageIsMoreThan", Integer.class),
    MUST_END("mustEnd", Boolean.class),


    //Composite
    HURT_PLAYER("hurtPlayer", EchoCommandsEnum.class, new EchoCommandsEnum[]{DAMAGE,CAN_KILL_PLAYER}),
    MOVE("move", EchoCommandsEnum.class, new EchoCommandsEnum[]{DIRECTION, SPEED}),
    GO_TO("goTo", EchoCommandsEnum.class, new EchoCommandsEnum[]{STEP, TIMES, IF_NO_KILLABLE_ALIVE, IF_PLAYER_DAMAGE_IS_MORE_THAN, IF_AT_LEAST_ONE_KILLABLE_ALIVE, IF_AT_LEAST_ONE_POI_EXAMINABLE, IF_PLAYER_DAMAGE_IS_LESS_THAN}),
    SPAWN("spawn", EchoCommandsEnum.class, new EchoCommandsEnum[]{IDENTIFIER, X, Y,RELATIVE});

    private final String commandString;
    private final Class<?> valueClass;
    private final EchoCommandsEnum[] subCommands;
    private final boolean isRequired;


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

