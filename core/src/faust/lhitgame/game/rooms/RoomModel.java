package faust.lhitgame.game.rooms;

import com.badlogic.gdx.math.Vector2;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.rooms.enums.RoomTypeEnum;

import java.util.Map;

/**
 * Simple room model
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomModel {

    public final Map<DirectionEnum, Vector2> boundaries;
    public final RoomTypeEnum type;

    public RoomModel(Map<DirectionEnum, Vector2> boundaries, RoomTypeEnum type) {
        this.boundaries = boundaries;
        this.type = type;
    }

}
