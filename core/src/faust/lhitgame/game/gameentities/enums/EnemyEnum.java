package faust.lhitgame.game.gameentities.enums;

public enum EnemyEnum {
    STRIX,
    BOUNDED,
    HIVE,
    SPITTER,
    MEAT,
    PORTAL,
    UNDEFINED;


    public static EnemyEnum getFromString(String name) {
        for (EnemyEnum e : EnemyEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
