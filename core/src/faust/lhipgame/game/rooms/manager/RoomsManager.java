package faust.lhipgame.game.rooms.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.instances.impl.PlayerInstance;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.rooms.AbstractRoom;
import faust.lhipgame.game.rooms.enums.RoomFlagEnum;
import faust.lhipgame.game.rooms.enums.RoomTypeEnum;
import faust.lhipgame.game.rooms.impl.CasualRoom;
import faust.lhipgame.game.rooms.impl.FixedRoom;
import faust.lhipgame.game.splash.SplashManager;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.game.world.manager.WorldManager;
import faust.lhipgame.saves.RoomSaveEntry;
import faust.lhipgame.saves.SaveFileManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Room Manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomsManager {

    private final SplashManager splashManager;
    private final AssetManager assetManager;
    private final SaveFileManager saveFileManager;

    private AbstractRoom currentRoom;
    private final Vector2 currentRoomPosInWorld = new Vector2(0, 0);

    /**
     * MainWorld Matrix
     */
    private final Map<Vector2, RoomTypeEnum> mainWorld = new HashMap<>();
    private final Map<Vector2, RoomSaveEntry> saveMap = new HashMap<>();
    private final Vector2 mainWorldSize = new Vector2(0, 0);

    private final WorldManager worldManager;
    private final TextBoxManager textManager;
    private final PlayerInstance player;
    private final OrthographicCamera camera;
    private final MusicManager musicManager;

    public RoomsManager(WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager, SaveFileManager saveFileManager, MusicManager musicManager) {
        this.worldManager = worldManager;
        this.textManager = textManager;
        this.splashManager = splashManager;
        this.assetManager = assetManager;
        this.saveFileManager = saveFileManager;
        this.musicManager = musicManager;
        this.player = player;
        this.camera = camera;

        initMainWorld();
        changeCurrentRoom(2, 0);
    }

    /**
     * Inits world from file
     */
    private void initMainWorld() {

        JsonValue terrains = new JsonReader().parse(Gdx.files.internal("mainWorldModel.json")).get("terrains");
        mainWorldSize.set(0, 0);

        terrains.forEach((t) -> {
            Vector2 v = new Vector2(t.getFloat("x"), t.getFloat("y"));
            RoomTypeEnum type = RoomTypeEnum.getFromString(t.getString("roomType"));
            Objects.requireNonNull(type);
            mainWorld.put(v, type);
            mainWorldSize.set(Math.max(mainWorldSize.x, v.x), Math.max(mainWorldSize.y, v.y));
        });
        // Finalize size
        mainWorldSize.set(mainWorldSize.x + 1, mainWorldSize.y + 1);

        //Try to load predefined casualnumbers for casual rooms from file
        try {
            saveFileManager.loadSaveForGame(player, saveMap);

        } catch (SerializationException ex) {
            Gdx.app.log("WARN", "No valid savefile to load");
        }
        //Init gamefile if no valid one has found
        saveFileManager.saveOnFile(player,saveMap);
    }

    /**
     * Changes the currentRoom
     *
     * @param newRoomPosX relative to the matrix of the world
     * @param newRoomPosY relative to the matrix of the world
     */
    public void changeCurrentRoom(int newRoomPosX, int newRoomPosY) {


        float finalX = (newRoomPosX < 0 ? mainWorldSize.x - 1 : (newRoomPosX == mainWorldSize.x ? 0 : newRoomPosX));
        float finalY = (newRoomPosY < 0 ? mainWorldSize.y - 1 : (newRoomPosY == mainWorldSize.y ? 0 : newRoomPosY));

        // Safety check on y
        if(finalY == 8 && finalX != 2){
            finalY--;
        }

        currentRoomPosInWorld.set(finalX, finalY);

        //Init room flags
        Map<RoomFlagEnum, Boolean> roomFlags = populateRoomFlags();

        int roomCasualNumber = 0;
        switch (mainWorld.get(currentRoomPosInWorld)) {
            case CASUAL: {
                currentRoom = new CasualRoom(worldManager, textManager, splashManager, player, camera, assetManager, saveMap.get(currentRoomPosInWorld), roomFlags, musicManager);
                // Save casualnumber in memory and prepare save on filesystem
                roomCasualNumber = ((CasualRoom) currentRoom).getCasualNumber();

                break;
            }
            default: {
                currentRoom = new FixedRoom(mainWorld.get(currentRoomPosInWorld), worldManager, textManager, splashManager, player, camera, assetManager, saveMap.get(currentRoomPosInWorld), roomFlags, musicManager);
                break;
            }
        }
        Gdx.app.log("DEBUG", "ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y);
        //Keep the same state of already visited rooms
        saveMap.put(currentRoomPosInWorld.cpy(),
                    new RoomSaveEntry(
                            (int) finalX,
                            (int) finalY,
                            roomCasualNumber,
                            roomFlags));


    }

    /**
     * @return populated map of flags
     */
    private Map<RoomFlagEnum, Boolean> populateRoomFlags() {
        //default map
        Map<RoomFlagEnum, Boolean> newRoomFlags = RoomFlagEnum.generateDefaultRoomFlags();

        if (RoomTypeEnum.CASUAL.equals(mainWorld.get(currentRoomPosInWorld))) {
            //If unvisited rooms are less than the number of found morgengabes to find, guarantee them
            final boolean guaranteedMorgengabe = player.getFoundMorgengabes() < 9 &&
                    (mainWorldSize.x * mainWorldSize.y) - 10 <= (saveMap.size() + (9 - player.getFoundMorgengabes()));
            newRoomFlags.put(RoomFlagEnum.GUARANTEED_MORGENGABE, guaranteedMorgengabe);

        } else if (RoomTypeEnum.hasEchoes(mainWorld.get(currentRoomPosInWorld))) {

            //If echoes were disabled in this room, disable them
            if (saveMap.containsKey(currentRoomPosInWorld)) {
                RoomSaveEntry entry = saveMap.get(currentRoomPosInWorld);
                newRoomFlags.put(RoomFlagEnum.DISABLED_ECHO, entry.savedFlags.get(RoomFlagEnum.DISABLED_ECHO));
            }

        }

        //Avoid showing more than one time enemy splash
        saveMap.forEach((key, entry) -> {
            if (entry.savedFlags.get(RoomFlagEnum.FIRST_BOUNDED_ENCOUNTERED)) {
                newRoomFlags.put(RoomFlagEnum.FIRST_BOUNDED_ENCOUNTERED, true);
            }
            if (entry.savedFlags.get(RoomFlagEnum.FIRST_STRIX_ENCOUNTERED)) {
                newRoomFlags.put(RoomFlagEnum.FIRST_STRIX_ENCOUNTERED, true);
            }
            if (entry.savedFlags.get(RoomFlagEnum.FIRST_HIVE_ENCOUNTERED)) {
                newRoomFlags.put(RoomFlagEnum.FIRST_HIVE_ENCOUNTERED, true);
            }
        });


        //Only bounded enemies after 15 rooms are visited
        newRoomFlags.put(RoomFlagEnum.GUARDANTEED_BOUNDED, saveMap.size() >= 15);

        //If this is the room visited, there should be no enemies even if they are in map
        newRoomFlags.put(RoomFlagEnum.DISABLED_ENEMIES, saveMap.size() < 2);


        return newRoomFlags;
    }

    /**
     * Wraps the room contents game logic
     *
     * @param stateTime
     */
    public void doRoomContentsLogic(float stateTime) {
        currentRoom.doRoomContentsLogic(stateTime);

        //Check if all poi have been examined
        saveMap.get(currentRoomPosInWorld).savedFlags.put(RoomFlagEnum.ALREADY_EXAMINED_POIS, currentRoom.arePoiCleared());

        // In final room should never change
        if (RoomTypeEnum.FINAL.equals(currentRoom.getRoomType())) {
            return;
        }

        // After room logic, handle the room change
        Vector2 playerPosition = player.getBody().getPosition();
        player.setStartX(playerPosition.x);
        player.setStartY(playerPosition.y);

        int newXPosInMatrix = (int) getCurrentRoomPosInWorld().x;
        int newYPosInMatrix = (int) getCurrentRoomPosInWorld().y;

        // Check for left or right passage
        if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY &&
                !RoomTypeEnum.CEMETERY_CENTER.equals(currentRoom.getRoomType()) &&
                !RoomTypeEnum.CEMETERY_TOP.equals(currentRoom.getRoomType())) {
            newXPosInMatrix--;
            player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);
        } else if ((playerPosition.x > AbstractRoom.RIGHT_BOUNDARY)) {
            newXPosInMatrix++;
            player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);
        }

        // Check for top or bottom passage
        if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY &&
                !RoomTypeEnum.CEMETERY_CENTER.equals(currentRoom.getRoomType()) &&
                !RoomTypeEnum.CEMETERY_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix--;
            player.setStartY(AbstractRoom.TOP_BOUNDARY - 4);
        } else if (playerPosition.y > AbstractRoom.TOP_BOUNDARY &&
                !RoomTypeEnum.CHURCH_LEFT.equals(currentRoom.getRoomType()) &&
                !RoomTypeEnum.CHURCH_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix++;
            player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 4);
        } else if (playerPosition.y > LHIPGame.GAME_HEIGHT * 0.45 &&
                RoomTypeEnum.CHURCH_ENTRANCE.equals(currentRoom.getRoomType())) {
            //Final room
            newXPosInMatrix = 2;
            newYPosInMatrix = 8;
            player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 8);
        }

        // Adjustments for world extremes, semi pacman effect 
        if (((playerPosition.x < AbstractRoom.LEFT_BOUNDARY) || (playerPosition.y > AbstractRoom.TOP_BOUNDARY)) &&
                getCurrentRoomPosInWorld().x == 0 &&
                getCurrentRoomPosInWorld().y == mainWorldSize.y - 1) {

            if (playerPosition.y > AbstractRoom.TOP_BOUNDARY) {
                player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 4);
            } else {
                player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);
            }

            newXPosInMatrix = (int) mainWorldSize.x - 1;
            newYPosInMatrix = 0;
        } else if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY &&
                getCurrentRoomPosInWorld().x == 0 &&
                getCurrentRoomPosInWorld().y == mainWorldSize.y - 2) {

            player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);

            newXPosInMatrix = (int) mainWorldSize.x - 1;
            newYPosInMatrix = 1;
        } else if (((playerPosition.x > AbstractRoom.RIGHT_BOUNDARY) || (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY)) &&
                getCurrentRoomPosInWorld().x == mainWorldSize.x - 1 &&
                getCurrentRoomPosInWorld().y == 0 &&
                !RoomTypeEnum.START_POINT.equals(currentRoom.getRoomType())) {

            if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY) {
                player.setStartY(AbstractRoom.TOP_BOUNDARY - 4);
            } else {
                player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);
            }

            newXPosInMatrix = 0;
            newYPosInMatrix = (int) (mainWorldSize.y - 1);
        } else if (playerPosition.x > AbstractRoom.RIGHT_BOUNDARY &&
                getCurrentRoomPosInWorld().x == mainWorldSize.x - 1 &&
                getCurrentRoomPosInWorld().y == 1) {

            player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);

            newXPosInMatrix = 0;
            newYPosInMatrix = (int) (mainWorldSize.y - 2);
        }

        //Change room and clear nearest poi reference
        if (getCurrentRoomPosInWorld().x != newXPosInMatrix || getCurrentRoomPosInWorld().y != newYPosInMatrix) {
            changeCurrentRoom(newXPosInMatrix, newYPosInMatrix);
            player.cleanReferences();
        }
    }

    /**
     * Draws all the POIs and the Decorations
     *
     * @param batch
     * @param stateTime
     */
    public void drawCurrentRoomContents(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);

        currentRoom.drawRoomContents(batch, stateTime);
    }

    /**
     * Draws the current room background terrain
     */
    public void drawCurrentRoomBackground() {
        currentRoom.drawRoomTerrain();
    }

    public Vector2 getCurrentRoomPosInWorld() {
        return currentRoomPosInWorld;
    }

    /**
     * Dispose current room contents
     */
    public void dispose() {
        saveFileManager.saveOnFile(player, saveMap);

        currentRoom.dispose();
    }

    /**
     * Draws the current room overlay tiles
     */
    public void drawCurrentRoomOverlays() {
        currentRoom.drawRoomOverlay();
    }
}
