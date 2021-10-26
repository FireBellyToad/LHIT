package faust.lhitgame.game.rooms.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Room flags enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum RoomFlagEnum {
    GUARANTEED_MORGENGABE, //There must be a retrievable morgengabe
    GUARDANTEED_BOUNDED, //There must be a Bounded enemy
    WITHOUT_HERBS, //Cannot have Herb POI inside
    GUARANTEED_HERBS, //There must be a Herb POI
    DISABLED_ENEMIES, //There must be no enemy in this room
    DISABLED_ECHO, //Echoes are disabled
    ALREADY_EXAMINED_POIS, //POIs are disabled
    FIRST_STRIX_ENCOUNTERED, //First Strix encountered
    FIRST_BOUNDED_ENCOUNTERED, //First Bounded encountered
    FIRST_HIVE_ENCOUNTERED, //First Hive encountered
    FINAL_ROOM;  //Final room of the game

    /**
     *
     * @return a default map of Flags
     */
    public static Map<RoomFlagEnum, Boolean> generateDefaultRoomFlags() {

            final Map<RoomFlagEnum, Boolean> roomFlags = new HashMap<>();
            for(RoomFlagEnum flag : values()){
                roomFlags.put(flag,false);
            }
            return roomFlags;

    }

    /**
     *
     * @param name
     * @return true if value string is valdi enum
     */
    public static boolean isValidFlag(String name){
        return !Objects.isNull(getFromString(name));
    }

    /**
     * Extract enum from string
     * @param name
     * @return
     */
    public static RoomFlagEnum getFromString(String name) {
        for (RoomFlagEnum e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
