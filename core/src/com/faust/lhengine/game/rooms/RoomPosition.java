package com.faust.lhengine.game.rooms;

/**
 * Class for Room position in mainWorld.
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomPosition {
    private final float x;
    private final float y;

    public RoomPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
