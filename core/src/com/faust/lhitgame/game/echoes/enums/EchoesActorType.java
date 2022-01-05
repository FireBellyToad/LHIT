package com.faust.lhitgame.game.echoes.enums;

public enum EchoesActorType {
    DISCIPULUS("echo.discipulus.json","sprites/discipulus_sheet.png", "sounds/terror.ogg"),
    VICTIM("echo.victim.json","sprites/victim_sheet.png",null),
    WOMAN("echo.woman.json","sprites/woman_sheet.png","sounds/horror_scream.ogg"),
    DEAD_HAND("echo.hand.json","sprites/hand_sheet.png", null),
    DEAD_DOUBLE_HAND("echo.hands.json","sprites/double_hand_sheet.png", "sounds/rattling-bones.ogg"),
    HORROR("echo.horror.json","sprites/horror_sheet.png", null),
    HORROR_BODY("echo.horrorbody.json","sprites/horror_body_sheet.png", "sounds/terror.ogg"),
    INFERNUM("echo.infernum.json","sprites/fleshpillar_sheet.png", "sounds/terror.ogg"),
    FLESH_PILLAR("echo.fleshpillar.json","sprites/fleshpillar_sheet.png", null),
    PORTAL_SPAWNER("echo.portal.json","sprites/fleshpillar_sheet.png", null);

    private final String filename;
    private final String spriteFilename;
    private final String soundFileName;

    EchoesActorType(String filename, String spriteFilename, String soundFileName) {
        this.filename = filename;
        this.spriteFilename = spriteFilename;
        this.soundFileName = soundFileName;
    }

    public String getFilename() {
        return filename;
    }

    public static EchoesActorType getFromString(String name) {
        for (EchoesActorType e : EchoesActorType.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public String getSpriteFilename() {
        return spriteFilename;
    }

    public String getSoundFileName() {
        return soundFileName;
    }
}
