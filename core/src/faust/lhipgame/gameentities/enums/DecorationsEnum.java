package faust.lhipgame.gameentities.enums;

public enum DecorationsEnum {
    PLANT,
    CROSS;

    public static DecorationsEnum getFromString(String name) {
        for (DecorationsEnum e : DecorationsEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
