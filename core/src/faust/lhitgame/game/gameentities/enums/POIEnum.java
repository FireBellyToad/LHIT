package faust.lhitgame.game.gameentities.enums;

public enum POIEnum {
    SKELETON("poi.skull",ItemEnum.GOLDCROSS,"splash.goldcross"),
    BUSH("poi.bush",ItemEnum.HEALTH_KIT),
    SOIL("poi.soil",ItemEnum.HOLY_LANCE,"splash.holy"),
    CADAVER("poi.cadaver",ItemEnum.ARMOR),
    BROTHER("poi.brother"),
    ECHO_CORPSE("poi.echocorpse"),
    MICHAEL("poi.michael", "splash.michael");

    private final String textKey;
    private final ItemEnum itemGiven;
    private final String splashKey;

    POIEnum(String textKey) {
        this(textKey, null, "");
    }
    POIEnum(String textKey, ItemEnum itemGiven, String splashKey) {
        this.textKey = textKey;
        this.itemGiven = itemGiven;
        this.splashKey = splashKey;
    }

    POIEnum(String textKey, ItemEnum itemGiven) {
        this(textKey, itemGiven, "");
    }

    POIEnum(String textKey, String splashKey) {
        this(textKey, null, splashKey);
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
}
