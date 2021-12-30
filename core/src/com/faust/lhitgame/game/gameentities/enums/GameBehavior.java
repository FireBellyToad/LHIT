package com.faust.lhitgame.game.gameentities.enums;

public enum GameBehavior {
    WALK,
    ATTACK,
    HURT,
    KNEE,
    IDLE,
    DEAD,
    LAYING,
    EVADE;

    public static GameBehavior getFromOrdinal(int ord) {
        return GameBehavior.values()[ord];
    }
}
