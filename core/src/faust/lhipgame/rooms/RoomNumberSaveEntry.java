package faust.lhipgame.rooms;

import java.io.Serializable;

/**
 * Wrapper Class for saving room number
 */
public class RoomNumberSaveEntry implements Serializable {

    public int casualNumber;
    public int x;
    public int y;
    public boolean poiCleared;

    public RoomNumberSaveEntry(int x, int y, int casualNumber, boolean poiCleared) {
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
        this.poiCleared = poiCleared;
    }


}
