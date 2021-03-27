package faust.lhipgame.rooms.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.RoomSaveEntry;

import java.util.Map;
import java.util.Objects;

/**
 * Save File Manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SaveFileManager {

    private final Json jsonParser = new Json();
    private String selectedFileName = "saves/mainWorldSave.json";

    public String getStringSaveFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap){

        //Player info
        String holyLanceEntry = getField("lance", player.getHolyLancePieces());
        String morgenabiumsEntry = getField("morgengabes", player.getFoundMorgengabes());
        String playerInfo = getField("playerInfo", holyLanceEntry + "," + morgenabiumsEntry, true);

        //Append rooms
        return "{" + playerInfo + "," + getField("rooms",jsonParser.toJson(saveMap.values())) + "}";
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
        return "\"" + fieldName + "\": " + (hasBrackets ? "{" + (String) fieldValue + "}" : fieldValue );
    }

    /**
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    private String getField(String fieldName, Object fieldValue) {
        return getField(fieldName,fieldValue,false);
    }

    /**
     *
     * @return filename
     */
    public String getFileName() {
        return selectedFileName;
    }

    public void loadSave(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) throws SerializationException {
        JsonValue file = new JsonReader().parse(Gdx.files.local(selectedFileName));

        if(Objects.isNull(file)){
            return;
        }
        //Player Info
        JsonValue playerInfo = file.get("playerInfo");
        if(Objects.isNull(playerInfo)){
            return;
        }
        player.setHolyLancePieces(playerInfo.getInt("lance"));
        player.setFoundMorgengabes(playerInfo.getInt("morgengabes"));

        //Already visited room Info
        JsonValue rooms = file.get("rooms");
        if(Objects.isNull(rooms)){
            return;
        }

        rooms.forEach((roomSaveEntry) -> {
            Vector2 v = new Vector2(roomSaveEntry.getFloat("x"), roomSaveEntry.getFloat("y"));
            int casualNumberPredefined = roomSaveEntry.getInt("casualNumber");
            boolean arePoiCleared = roomSaveEntry.getBoolean("poiCleared");
            Objects.requireNonNull(casualNumberPredefined);

            saveMap.put(v, new RoomSaveEntry(
                    (int) v.x, (int) v.y, casualNumberPredefined,
                    arePoiCleared));
        });
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
}
