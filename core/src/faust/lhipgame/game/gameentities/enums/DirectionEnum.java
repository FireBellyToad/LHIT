package faust.lhipgame.game.gameentities.enums;

public enum DirectionEnum {
    UP, RIGHT, DOWN, LEFT, UNUSED;


    public static DirectionEnum getFromString(String name) {
        for (DirectionEnum e : DirectionEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
    }
