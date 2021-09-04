package faust.lhitgame.saves;

import faust.lhitgame.game.rooms.enums.RoomFlagEnum;

import java.io.Serializable;
import java.util.Map;

/**
 * Wrapper Class for saving room
 */
public class RoomSaveEntry implements Serializable {

    public final int casualNumber;
    public final int x;
    public final int y;
    public final Map<RoomFlagEnum,Boolean> savedFlags;

    public RoomSaveEntry(int x, int y, int casualNumber, Map<RoomFlagEnum,Boolean> savedFlags) {
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
        this.savedFlags = savedFlags;
    }

}
