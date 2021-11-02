package faust.lhitgame.game.gameentities.enums;

public enum DecorationsEnum {
    PLANT,
    CROSS_WOOD,
    CROSS_IRON,
    STONE_1,
    STONE_2,
    GRASS,
    TREE_STUMP,
    BOAT,
    ALLY_CORPSE_1,
    ALLY_CORPSE_2,
    TUMOR,
    IMPALED;

    public static DecorationsEnum getFromString(String name) {
        for (DecorationsEnum e : DecorationsEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
