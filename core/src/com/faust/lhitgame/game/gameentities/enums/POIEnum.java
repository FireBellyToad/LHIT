package com.faust.lhitgame.game.gameentities.enums;

public enum POIEnum {
    SKELETON("poi.skull", ItemEnum.GOLDCROSS, "splash.goldcross"),
    BUSH("poi.bush", ItemEnum.HEALTH_KIT),
    SOIL("poi.soil", ItemEnum.HOLY_LANCE, "splash.holy"),
    CADAVER("poi.cadaver", ItemEnum.ARMOR),
    BROTHER("poi.brother"),
    ECHO_CORPSE("poi.echocorpse"),
    MICHAEL("poi.michael", ItemEnum.STATUE, "splash.michael"),
    BURNT_PAPER("poi.burnt"),
    BAPTISMAL("poi.baptismal", true);

    private final String textKey;
    private final ItemEnum itemGiven;
    private final String splashKey;
    private final boolean mustTriggerAfterExamination;

    POIEnum(String textKey) {
        this(textKey, null, "", false);
    }

    POIEnum(String textKey, boolean mustTriggerAfterExamination) {
        this(textKey, null, "", mustTriggerAfterExamination);
    }

    POIEnum(String textKey, ItemEnum itemGiven, String splashKey, boolean mustTriggerAfterExamination) {
        this.textKey = textKey;
        this.itemGiven = itemGiven;
        this.splashKey = splashKey;
        this.mustTriggerAfterExamination = mustTriggerAfterExamination;
    }

    POIEnum(String textKey, ItemEnum itemGiven) {
        this(textKey, itemGiven, "", false);
    }

    POIEnum(String textKey, ItemEnum itemGiven, String splashKey) {
        this(textKey, itemGiven, splashKey, false);
    }

    public String getTextKey() {
        return textKey;
    }

    public ItemEnum getItemGiven() {
        return itemGiven;
    }

    public String getSplashKey() {
        return splashKey;
    }

    public boolean mustTriggerAfterExamination() {
        return mustTriggerAfterExamination;
    }
}
