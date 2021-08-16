package faust.lhipgame.saves;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import faust.lhipgame.game.instances.impl.PlayerInstance;
import faust.lhipgame.game.rooms.enums.RoomFlagEnum;

import java.util.*;

/**
 * Save File Manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SaveFileManager {

    private final Json jsonParser = new Json();
    private final String selectedFileName = "saves/mainWorldSave.json";

    public String getStringSaveFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) {

        //Player info
        List<String> entries = new ArrayList<>();
        entries.add(getField("lance", player.getHolyLancePieces()));
        entries.add(getField("morgengabes", player.getFoundMorgengabes()));
        entries.add(getField("armor", player.hasArmor()));
        entries.add(getField("damage", player.getDamage()));

        String playerInfo = getField("playerInfo", String.join(",", entries), true);

        //Append rooms
        return "{" + playerInfo + "," + getField("rooms", jsonParser.toJson(saveMap.values())) + "}";
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
        JsonValue file = new JsonReader().parse(Gdx.files.local(selectedFileName));

        if (Objects.isNull(file)) {
            return;
        }
        //Player Info
        setPlayerInfo(player, file);

        //Room Info
        setRoomInfo(saveMap, file);
    }

    /**
     * Set room info in savemap
     *
     * @param saveMap
     * @param file
     */
    private void setRoomInfo(Map<Vector2, RoomSaveEntry> saveMap, JsonValue file) {

        //Already visited room Info
        JsonValue rooms = file.get("rooms");
        if (Objects.isNull(rooms)) {
            return;
        }

        //Main loop
        Vector2 roomPositionInCurrentSave;
        int casualNumberPredefined;
        JsonValue flagsJson;

        for (JsonValue roomSaveEntry : rooms) {
            roomPositionInCurrentSave = new Vector2(roomSaveEntry.getFloat("x"), roomSaveEntry.getFloat("y"));

            casualNumberPredefined = roomSaveEntry.getInt("casualNumber");

            flagsJson = roomSaveEntry.get("savedFlags");

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

        JsonValue playerInfo = file.get("playerInfo");
        if (Objects.isNull(playerInfo)) {
            return;
        }
        player.setHolyLancePieces(playerInfo.getInt("lance"));
        player.setFoundMorgengabes(playerInfo.getInt("morgengabes"));
        player.setHasArmor(playerInfo.getBoolean("armor"));
        player.setDamage(playerInfo.getInt("damage"));

    }

    /**
     * Save on filesystem the predefined numbers of the casual rooms
     */
    public void saveOnFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) {

        String stringSave = getStringSaveFile(player, saveMap);
        Gdx.files.local(getFileName()).writeString(stringSave, false);
    }

    /**
     * Clean saveFile for new game
     */
    public void cleanSaveFile() {

        Gdx.files.local(getFileName()).writeString("", false);
    }

    /**
     * Load game and put raw loaded values in map
     * @return
     */
    public Map<String, Object> loadRawValues() {

        FileHandle file = Gdx.files.local(selectedFileName);

        JsonValue fileRead = null;
        try {
            fileRead= new JsonReader().parse(file);
        } catch (Exception e){
            Gdx.app.log("DEBUG", e.getMessage());
        }

        if (Objects.isNull(fileRead)) {
            return null;
        }

        JsonValue playerInfo = fileRead.get("playerInfo");
        if (Objects.isNull(playerInfo)) {
            return null;
        }

        Map<String,Object> rawValuesMap = new HashMap<>();

        rawValuesMap.put("lance", playerInfo.getInt("lance"));
        rawValuesMap.put("morgengabes",playerInfo.getInt("morgengabes"));
        rawValuesMap.put("armor", playerInfo.getBoolean("armor"));

        return rawValuesMap;
    }
}
