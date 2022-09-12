package com.faust.lhengine.game.rooms;

import java.util.Objects;

/**
 * Class for Room position in mainWorld.
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomPosition {
    private final int x;
    private final int y;

    public RoomPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomPosition)) return false;
        RoomPosition that = (RoomPosition) o;
        return x == that.x && y == that.y;
    }

    @Override
    public String toString() {
        return "\"x\": " + x + ", \"y\" :" + y;
    }
}
