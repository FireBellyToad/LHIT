package faust.lhipgame.gameentities.enums;

public enum DecorationsEnum {
    PLANT,
    CROSS_WOOD,
    CROSS_IRON,
    STONE_1,
    STONE_2,
    GRASS;

    public static DecorationsEnum getFromString(String name) {
        for (DecorationsEnum e : DecorationsEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
