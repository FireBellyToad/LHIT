package faust.lhipgame.game.gameentities.enums;

public enum POIEnum {
    SKELETON("poi.skull",ItemEnum.MORGENGABE,"splash.morgengabe"),
    BUSH("poi.bush",ItemEnum.HEALTH_KIT),
    SOIL("poi.soil",ItemEnum.HOLY_LANCE,"splash.holy"),
    CADAVER("poi.cadaver",ItemEnum.ARMOR),
    ALLY("poi.brother",ItemEnum.HEALTH_KIT);

    private final String textKey;
    private final ItemEnum itemGiven;
    private final String splashKey;

    POIEnum(String textKey, ItemEnum itemGiven, String splashKey) {
        this.textKey = textKey;
        this.itemGiven = itemGiven;
        this.splashKey = splashKey;
    }

    POIEnum(String textKey, ItemEnum itemGiven) {
        this(textKey, itemGiven, "");
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

    public static POIEnum getFromString(String name) {
        for (POIEnum e : POIEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
