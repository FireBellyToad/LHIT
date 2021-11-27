package faust.lhitgame.game.rooms;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.instances.impl.DecorationInstance;
import faust.lhitgame.game.instances.impl.POIInstance;
import faust.lhitgame.game.instances.impl.PlayerInstance;
import faust.lhitgame.game.rooms.areas.EmergedArea;
import faust.lhitgame.game.rooms.areas.WallArea;
import faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import faust.lhitgame.game.rooms.enums.RoomTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * Model class for storing Rooom contents
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomContent {

    public  List<POIInstance> poiList;
    public  List<DecorationInstance> decorationList;
    public  List<AnimatedInstance> enemyList;
    public  List<WallArea> wallList;
    public  List<EmergedArea> emergedAreaList;
    public  PlayerInstance player;
    public  RoomTypeEnum roomType;
    public String roomFileName;

    public Map<RoomFlagEnum, Boolean> roomFlags;
}
