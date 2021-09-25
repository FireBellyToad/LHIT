package faust.lhitgame.game.gameentities.enums;

public enum GameBehavior {
    WALK,
    ATTACK,
    HURT,
    KNEE,
    IDLE,
    DEAD,
    LAYING,
    EVADE;

    public static GameBehavior getFromString(String name) {
        for (GameBehavior e : GameBehavior.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static GameBehavior getFromOrdinal(int ord) {
        return GameBehavior.values()[ord];
    }
}
