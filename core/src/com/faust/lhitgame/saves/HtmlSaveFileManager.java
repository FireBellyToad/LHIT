package com.faust.lhitgame.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import com.faust.lhitgame.saves.enums.SaveFieldsEnum;
import com.faust.lhitgame.saves.interfaces.SaveFileManager;

import java.util.*;

/**
 * HTML Save File Manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class HtmlSaveFileManager implements SaveFileManager {

    private static final String ROOT_KEY = "save";
    private final Json jsonParser = new Json();
    private final String selectedFileName = "saves/mainWorldSave";

    public String getStringSaveFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) {

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
    private String getField(String fieldName, Object fieldValue, boolean hasBrackets) {
        return "\"" + fieldName + "\": " + (hasBrackets ? "{" + fieldValue + "}" : fieldValue);
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private String getField(String fieldName, Object fieldValue) {
        return getField(fieldName, fieldValue, false);
    }

    /**
     * @return filename
     */
    public String getFileName() {
        return selectedFileName;
    }

    /**
     * Load game and populate game instances with the loaded values
     *
     * @param player
     * @param saveMap
     * @throws SerializationException
     */
    public void loadSaveForGame(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) throws SerializationException {

        Preferences file = Gdx.app.getPreferences(selectedFileName);

        String content = (String) file.get().get(ROOT_KEY);

        if (Objects.isNull(content)) {
            return;
        }

        JsonValue fileContent = new JsonReader().parse(content);

        if (Objects.isNull(fileContent)) {
            return;
        }
        //Player Info
        setPlayerInfo(player, fileContent);

        //Room Info
        setRoomInfo(saveMap, fileContent);
    }

    /**
     * Set room info in savemap
     *
     * @param saveMap
     * @param file
     */
    private void setRoomInfo(Map<Vector2, RoomSaveEntry> saveMap, JsonValue file) {

        //Already visited room Info
        JsonValue rooms = file.get(SaveFieldsEnum.ROOMS.getFieldName());
        if (Objects.isNull(rooms)) {
            return;
        }

        //Main loop
        Vector2 roomPositionInCurrentSave;
        int casualNumberPredefined;
        JsonValue flagsJson;

        for (JsonValue roomSaveEntry : rooms) {
            roomPositionInCurrentSave = new Vector2(roomSaveEntry.getFloat(SaveFieldsEnum.X.getFieldName()),
                    roomSaveEntry.getFloat(SaveFieldsEnum.Y.getFieldName()));

            casualNumberPredefined = roomSaveEntry.getInt(SaveFieldsEnum.CASUAL_NUMBER.getFieldName());

            flagsJson = roomSaveEntry.get(SaveFieldsEnum.SAVED_FLAGS.getFieldName());

            saveMap.put(roomPositionInCurrentSave, new RoomSaveEntry(
                    (int) roomPositionInCurrentSave.x, (int) roomPositionInCurrentSave.y, casualNumberPredefined,
                    parseJsonFlags(flagsJson)));
        }
    }

    /**
     * Parse saved flag
     *
     * @param flagsJson to parse
     * @return parsed flags
     */
    private Map<RoomFlagEnum, Boolean> parseJsonFlags(JsonValue flagsJson) {
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
    private void setPlayerInfo(PlayerInstance player, JsonValue file) {

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
     * Save on filesystem the predefined numbers of the casual rooms
     */
    public void saveOnFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) {

        String stringSave = getStringSaveFile(player, saveMap);
        Gdx.app.getPreferences(getFileName()).putString(ROOT_KEY, stringSave);
    }

    /**
     * Clean saveFile for new game
     */
    public void cleanSaveFile() {

        Gdx.app.getPreferences(getFileName()).clear();
    }

    /**
     * Load game and put raw loaded values in map
     *
     * @return
     */
    public Map<String, Object> loadRawValues() {

        Preferences fileRead = Gdx.app.getPreferences(selectedFileName);

        if (Objects.isNull(fileRead)) {
            return null;
        }

        String content = (String) fileRead.get().get(ROOT_KEY);

        if (Objects.isNull(content)) {
            return null;
        }

        JsonValue root = new JsonReader().parse(content);

        if (Objects.isNull(root)) {
            return null;
        }
        JsonValue playerInfo = root.get(SaveFieldsEnum.PLAYER_INFO.getFieldName());
        if (Objects.isNull(playerInfo)) {
            return null;
        }

        Map<String, Object> rawValuesMap = new HashMap<>();

        //All subfields excluding Armor (which is not int)
        for (SaveFieldsEnum subField : SaveFieldsEnum.PLAYER_INFO.getSubFields()) {
            if (!SaveFieldsEnum.ARMOR.equals(subField)) {
                rawValuesMap.put(subField.getFieldName(), playerInfo.getInt(subField.getFieldName()));
            }
        }

        rawValuesMap.put(SaveFieldsEnum.ARMOR.getFieldName(), playerInfo.getBoolean(SaveFieldsEnum.ARMOR.getFieldName()));

        return rawValuesMap;
    }
}
