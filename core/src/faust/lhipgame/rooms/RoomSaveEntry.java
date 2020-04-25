package faust.lhipgame.rooms;

import com.badlogic.gdx.math.MathUtils;
import faust.lhipgame.rooms.enums.RoomType;

import java.io.Serializable;
import java.util.Objects;

/**
 * Wrapper Class for Room Type enum
 */
public class RoomSaveEntry implements Serializable {

    private RoomType roomType;
    private int casualNumber;
    private int x;
    private int y;

    public RoomSaveEntry(RoomType roomType, int x, int y) {
        this.roomType = roomType;
        this.casualNumber = 0;
        this.x = x;
        this.y = y;
    }

    public RoomSaveEntry(RoomType roomType, int x, int y, int casualNumber) {
        this.roomType = roomType;
        this.casualNumber = casualNumber;
        this.x = x;
        this.y = y;
    }
}
