package faust.lhipgame.gameentities.enums;

public enum POIEnum {
    SKELETON("poi.skull.examine");

    private String textKey;

    POIEnum(String textKey) {
        this.textKey = textKey;
    }

    public String getTextKey() {
        return textKey;
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
