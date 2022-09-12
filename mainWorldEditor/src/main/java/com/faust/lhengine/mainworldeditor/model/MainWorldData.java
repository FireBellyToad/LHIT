package com.faust.lhengine.mainworldeditor.model;

import com.faust.lhengine.game.gameentities.enums.DirectionEnum;
import com.faust.lhengine.game.rooms.RoomModel;
import com.faust.lhengine.game.rooms.RoomPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Main World Data
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MainWorldData {

    private final Map<RoomPosition, RoomModel> data = new HashMap<>();

    public Map<RoomPosition, RoomModel> getData() {
        return data;
    }

    /**
     * FIXME improve with a proper library!
     *
     * @return a json of the room data
     */
    public String toJson() {
        String json = "{ \"terrains\": [ ";

        //Rooms data
        for (Map.Entry<RoomPosition, RoomModel> entry : data.entrySet()) {
            json += "{ ";

            json += "\"type\": \"" + entry.getValue().type.name() + "\", ";
            json += entry.getKey() + " ";

            //Boundaries
            if (!entry.getValue().boundaries.isEmpty()) {

                for (Map.Entry<DirectionEnum, RoomPosition> boundary : entry.getValue().boundaries.entrySet()) {
                    json += "{ ";
                    json += "\"side\": \"" + boundary.getKey().name() + "\", ";
                    json += "\"target\": ";

                    if (Objects.isNull(boundary.getValue())) {
                        json += "null ";
                    } else {
                        json += "{ \"";
                        json += boundary.getValue() + " ";
                        json += "} ";
                    }

                    json += "} ";
                    json += "} ";
                }
            }

            json += "}, ";
        }

        //remove last comma
        json = json.substring(0,json.lastIndexOf(","));

        json += " ] }";
        return json;
    }

    public void clear() {
        data.clear();
    }
}
