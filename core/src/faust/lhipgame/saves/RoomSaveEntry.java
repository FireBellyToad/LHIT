package faust.lhipgame.saves;

import faust.lhipgame.game.rooms.enums.RoomFlagEnum;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Wrapper Class for saving room
 */
public class RoomSaveEntry implements Serializable {

    public int casualNumber;
    public int x;
    public int y;
    public Map<RoomFlagEnum,Boolean> savedFlags;

    public RoomSaveEntry(int x, int y, int casualNumber, Map<RoomFlagEnum,Boolean> savedFlags) {
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
        this.savedFlags = savedFlags;
    }

}
