package com.faust.lhitgame.saves.interfaces;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.SerializationException;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.saves.RoomSaveEntry;

import java.util.Map;

 public interface SaveFileManager {
     String getStringSaveFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap);

    /**
     * @return filename
     */
     String getFileName();

    /**
     * Load game and populate game instances with the loaded values
     *
     * @param player
     * @param saveMap
     * @throws SerializationException
     */
     void loadSaveForGame(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap) throws SerializationException;

    /**
     * Save on filesystem the predefined numbers of the casual rooms
     */
     void saveOnFile(PlayerInstance player, Map<Vector2, RoomSaveEntry> saveMap);

    /**
     * Clean saveFile for new game
     */
     void cleanSaveFile();

    /**
     * Load game and put raw loaded values in map
     *
     * @return
     */
     Map<String, Object> loadRawValues();
}
