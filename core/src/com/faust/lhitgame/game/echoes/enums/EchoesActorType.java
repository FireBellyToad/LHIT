package com.faust.lhitgame.game.echoes.enums;

public enum EchoesActorType {
    DISCIPULUS("echo.discipulus.lua","sprites/discipulus_sheet.png", "sounds/terror.ogg"),
    VICTIM("echo.victim.lua","sprites/victim_sheet.png",null),
    WOMAN("echo.woman.lua","sprites/woman_sheet.png","sounds/horror_scream.ogg"),
    DEAD_HAND("echo.hand.lua","sprites/hand_sheet.png", null),
    DEAD_DOUBLE_HAND("echo.hands.lua","sprites/double_hand_sheet.png", "sounds/rattling-bones.ogg"),
    HORROR("echo.horror.lua","sprites/horror_sheet.png", null),
    HORROR_BODY("echo.horrorbody.lua","sprites/horror_body_sheet.png", "sounds/terror.ogg"),
    INFERNUM("echo.infernum.lua","sprites/fleshpillar_sheet.png", "sounds/terror.ogg"),
    FLESH_PILLAR("echo.fleshpillar.lua","sprites/fleshpillar_sheet.png", null),
    PORTAL_SPAWNER("echo.portal.lua","sprites/fleshpillar_sheet.png", null);

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
