package faust.lhipgame.gameentities.enums;

import faust.lhipgame.rooms.enums.RoomType;

public enum GameBehavior {
    WALK,
    ATTACK,
    HURT,
    KNEE,
    IDLE;

    public static GameBehavior getFromString(String name) {
        for (GameBehavior e : GameBehavior.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
