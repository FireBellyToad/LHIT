package com.faust.lhengine.game.rooms;

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
}
