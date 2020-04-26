package faust.lhipgame.rooms;

import java.io.Serializable;

/**
 * Wrapper Class for saving room number
 */
public class CasualRoomNumberSaveEntry implements Serializable {

    private int casualNumber;
    private int x;
    private int y;

    public CasualRoomNumberSaveEntry(int x, int y, int casualNumber) {
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
    }

}
