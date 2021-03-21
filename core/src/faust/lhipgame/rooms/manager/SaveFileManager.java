package faust.lhipgame.rooms.manager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.RoomSaveEntry;

import java.util.Map;

/**
 * Save File Manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SaveFileManager {

    private final Json jsonParser = new Json();

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
        //TODO valuare diversi filname
        return "saves/mainWorldSave.json";
    }
}
