package com.faust.lhitgame.saves;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import com.faust.lhitgame.saves.RoomSaveEntry;
import com.faust.lhitgame.saves.enums.SaveFieldsEnum;

import java.util.*;

/**
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AbstractSaveFileManager {

    protected static final String ROOT_KEY = "save";
    protected final Json jsonParser = new Json();
    protected final String selectedFileName = "saves/mainWorldSave";

    protected String getStringSaveFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) {

        //Player info
        List<String> entries = new ArrayList<>();
        entries.add(getField(SaveFieldsEnum.LANCE.getFieldName(), player.getHolyLancePieces()));
        entries.add(getField(SaveFieldsEnum.CROSSES.getFieldName(), player.getFoundCrosses()));
        entries.add(getField(SaveFieldsEnum.ARMOR.getFieldName(), player.hasArmor()));
        entries.add(getField(SaveFieldsEnum.DAMAGE.getFieldName(), player.getDamage()));
        entries.add(getField(SaveFieldsEnum.HERBS_FOUND.getFieldName(), player.getHerbsFound()));
        entries.add(getField(SaveFieldsEnum.HERBS_AVAILABLE.getFieldName(), player.getAvailableHealthKits()));

        String playerInfo = getField(SaveFieldsEnum.PLAYER_INFO.getFieldName(), String.join(",", entries), true);

        //Append rooms
        return "{" + playerInfo + "," + getField(SaveFieldsEnum.ROOMS.getFieldName(), jsonParser.toJson(saveMap.values())) + "}";
    }

    /**
     * Get string with
     *
     * @param fieldName
     * @param fieldValue
     * @param hasBrackets
     * @return
     */
    protected String getField(String fieldName, Object fieldValue, boolean hasBrackets) {
        return "\"" + fieldName + "\": " + (hasBrackets ? "{" + fieldValue + "}" : fieldValue);
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @return
     */
    protected String getField(String fieldName, Object fieldValue) {
        return getField(fieldName, fieldValue, false);
    }

    /**
     * Set room info in savemap
     *
     * @param saveMap
     * @param file
     */
    protected void setRoomInfo(Map<Vector2, RoomSaveEntry> saveMap, JsonValue file) {

        //Already visited room Info
        JsonValue rooms = file.get(SaveFieldsEnum.ROOMS.getFieldName());
        if (Objects.isNull(rooms)) {
            return;
        }

        //Main loop
        Vector2 roomPositionInCurrentSave;
        int casualNumberPredefined;
        JsonValue flagsJson;
        JsonValue poiStateJson;

        for (JsonValue roomSaveEntry : rooms) {
            roomPositionInCurrentSave = new Vector2(roomSaveEntry.getFloat(SaveFieldsEnum.X.getFieldName()),
                    roomSaveEntry.getFloat(SaveFieldsEnum.Y.getFieldName()));

            casualNumberPredefined = roomSaveEntry.getInt(SaveFieldsEnum.CASUAL_NUMBER.getFieldName());

            flagsJson = roomSaveEntry.get(SaveFieldsEnum.SAVED_FLAGS.getFieldName());
            poiStateJson = roomSaveEntry.get(SaveFieldsEnum.POI_STATES.getFieldName());

            saveMap.put(roomPositionInCurrentSave, new RoomSaveEntry(
                    (int) roomPositionInCurrentSave.x, (int) roomPositionInCurrentSave.y, casualNumberPredefined,
                    parseJsonFlags(flagsJson), parseJsonPoiState(poiStateJson)));
        }
    }

    /**
     * @param poiStateJson
     * @return
     */
    protected Map<Integer, Boolean> parseJsonPoiState(JsonValue poiStateJson) {
        Map<Integer, Boolean> map = new HashMap<>();

        if (Objects.nonNull(poiStateJson.child()) && Objects.nonNull(poiStateJson.child().next())) {

            //Extract child (skipping class name)
            JsonValue child = poiStateJson.child().next();

            do {
                map.put(Integer.valueOf(child.name()), child.asBoolean());
                child = child.next();
            } while (Objects.nonNull(child));
        }

        return map;
    }


    /**
     * Parse saved flag
     *
     * @param flagsJson to parse
     * @return parsed flags
     */
    protected Map<RoomFlagEnum, Boolean> parseJsonFlags(JsonValue flagsJson) {
        Map<RoomFlagEnum, Boolean> map = RoomFlagEnum.generateDefaultRoomFlags();
        boolean extractedValue;

        for (RoomFlagEnum realFlag : RoomFlagEnum.values()) {
            extractedValue = flagsJson.getBoolean(realFlag.name());
            map.put(realFlag, extractedValue);
        }

        return map;
    }

    /**
     * Sets player info from file
     *
     * @param player
     * @param file
     */
    protected void setPlayerInfo(PlayerInstance player, JsonValue file) {

        JsonValue playerInfo = file.get(SaveFieldsEnum.PLAYER_INFO.getFieldName());
        if (Objects.isNull(playerInfo)) {
            return;
        }
        player.setHolyLancePieces(playerInfo.getInt(SaveFieldsEnum.LANCE.getFieldName()));
        player.setFoundCrosses(playerInfo.getInt(SaveFieldsEnum.CROSSES.getFieldName()));
        player.setHasArmor(playerInfo.getBoolean(SaveFieldsEnum.ARMOR.getFieldName()));
        player.setDamage(playerInfo.getInt(SaveFieldsEnum.DAMAGE.getFieldName()));
        player.setHerbsFound(playerInfo.getInt(SaveFieldsEnum.HERBS_FOUND.getFieldName()));
        player.setAvailableHealthKits(playerInfo.getInt(SaveFieldsEnum.HERBS_AVAILABLE.getFieldName()));

    }

    /**
     * Load game and populate game instances with the loaded values
     *
     * @param player
     * @param saveMap
     * @throws SerializationException
     */
    public abstract void loadSaveForGame(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) throws SerializationException;

    /**
     * Save on filesystem the predefined numbers of the casual rooms
     */
    public abstract void saveOnFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap);

    /**
     * Clean saveFile for new game
     */
    public abstract void cleanSaveFile();

    /**
     * Load game and put raw loaded values in map
     *
     * @return
     */
    public abstract Map<String, Object> loadRawValues();
}
