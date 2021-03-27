package faust.lhipgame.saves;

import java.io.Serializable;

/**
 * Wrapper Class for saving room
 */
public class RoomSaveEntry implements Serializable {

    public int casualNumber;
    public int x;
    public int y;
    public boolean poiCleared;

    public RoomSaveEntry(int x, int y, int casualNumber, boolean poiCleared) {
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
        this.poiCleared = poiCleared;
    }


}
