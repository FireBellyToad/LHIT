package com.faust.lhengine.mainworldeditor.model;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonSerializer;
import com.esotericsoftware.jsonbeans.OutputType;
import com.faust.lhengine.game.gameentities.enums.DirectionEnum;
import com.faust.lhengine.game.rooms.MainWorldModel;
import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;
import com.faust.lhengine.mainworldeditor.serialization.MainWorldSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Main World Data
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldData {

    public final Map<RoomPosition, RoomModel> terrains = new HashMap<>();

    /**
     *
     * @return a json of the room data
     */
    public String toJson() {
        final Json jsonParser = new Json();
        jsonParser.setSerializer(MainWorldData.class, new MainWorldSerializer());
        jsonParser.setOutputType(OutputType.json);

        String mapJson = jsonParser.prettyPrint(this);

        return mapJson;
    }

    public void clear() {
        terrains.clear();
    }
}
