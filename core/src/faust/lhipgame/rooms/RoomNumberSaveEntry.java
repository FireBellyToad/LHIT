package faust.lhipgame.rooms;

import java.io.Serializable;

/**
 * Wrapper Class for saving room number
 */
public class RoomNumberSaveEntry implements Serializable {

    private int casualNumber;
    private int x;
    private int y;

    public RoomNumberSaveEntry(int x, int y, int casualNumber) {
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
    }

}
