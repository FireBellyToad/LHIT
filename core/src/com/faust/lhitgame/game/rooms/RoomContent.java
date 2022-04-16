package com.faust.lhitgame.game.rooms;

import com.faust.lhitgame.game.ai.RoomNodesGraph;
import com.faust.lhitgame.game.instances.AnimatedInstance;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.impl.DecorationInstance;
import com.faust.lhitgame.game.instances.impl.EchoActorInstance;
import com.faust.lhitgame.game.instances.impl.POIInstance;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.rooms.areas.EmergedArea;
import com.faust.lhitgame.game.rooms.areas.WallArea;
import com.faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import com.faust.lhitgame.game.rooms.enums.RoomTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * Model class for storing Rooom contents
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomContent {

    public List<POIInstance> poiList;
    public List<DecorationInstance> decorationList;
    public List<AnimatedInstance> enemyList;
    public List<WallArea> wallList;
    public List<EmergedArea> emergedAreaList;
    public PlayerInstance player;
    public RoomTypeEnum roomType;
    public String roomFileName;
    public List<EchoActorInstance> echoActors; //Can have Echo actors
    public List<GameInstance> spellEffects;

    public Map<RoomFlagEnum, Boolean> roomFlags;
    public RoomNodesGraph roomGraph;
}
