package faust.lhipgame.rooms.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.RoomSaveEntry;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.rooms.impl.CasualRoom;
import faust.lhipgame.rooms.impl.FixedRoom;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextBoxManager;
import faust.lhipgame.world.manager.WorldManager;

import java.util.*;

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
    private final Map<Vector2, RoomType> mainWorld = new HashMap<>();
    private final Map<Vector2, RoomSaveEntry> saveMap = new HashMap<>();
    private final Vector2 mainWorldSize = new Vector2(0, 0);

    private WorldManager worldManager;
    private TextBoxManager textManager;
    private PlayerInstance player;
    private OrthographicCamera camera;

    public RoomsManager(WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager, SaveFileManager saveFileManager) {
        this.worldManager = worldManager;
        this.textManager = textManager;
        this.splashManager = splashManager;
        this.assetManager = assetManager;
        this.saveFileManager = saveFileManager;
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
            RoomType type = RoomType.getFromString(t.getString("roomType"));
            Objects.requireNonNull(type);
            mainWorld.put(v, type);
            mainWorldSize.set(Math.max(mainWorldSize.x, v.x), Math.max(mainWorldSize.y, v.y));
        });
        // Finalize size
        mainWorldSize.set(mainWorldSize.x + 1, mainWorldSize.y + 1);

        //Try to load predefined casualnumbers for casual rooms from file
        try {
            saveFileManager.loadSave(player, saveMap);

        } catch (SerializationException ex) {
            Gdx.app.log("WARN", "No valid savefile to load");
        }
    }

    /**
     * Changes the currentRoom
     *
     * @param newRoomPosX relative to the matrix of the world
     * @param newRoomPosY relative to the matrix of the world
     */
    public void changeCurrentRoom(int newRoomPosX, int newRoomPosY) {

        // Dispose the current room contents if not null
        if (!Objects.isNull(currentRoom)) {
            currentRoom.dispose();
        }

        float finalX = (newRoomPosX < 0 ? mainWorldSize.x - 1 : (newRoomPosX == mainWorldSize.x ? 0 : newRoomPosX));
        float finalY = (newRoomPosY < 0 ? mainWorldSize.y - 1 : (newRoomPosY == mainWorldSize.y ? 0 : newRoomPosY));

        currentRoomPosInWorld.set(finalX, finalY);

        int roomCasualNumber = 0;
        switch (mainWorld.get(currentRoomPosInWorld)) {
            case CASUAL: {

                //If unvisited rooms are less than the number of found morgengabes to find, guarantee them
                boolean guaranteedMorgengabe = player.getFoundMorgengabes() < 9 &&
                        (mainWorldSize.x * mainWorldSize.y)-10  <= (saveMap.size() + (9 -player.getFoundMorgengabes() ));
                currentRoom = new CasualRoom(worldManager, textManager, splashManager, player, camera, assetManager, saveMap.get(currentRoomPosInWorld), guaranteedMorgengabe);

                Gdx.app.log("DEBUG","(mainWorldSize.x * mainWorldSize.y)-10: " +((mainWorldSize.x * mainWorldSize.y)-10));
                Gdx.app.log("DEBUG","(saveMap.size() + (9 -player.getFoundMorgengabes() )): " +(saveMap.size() + (9 -player.getFoundMorgengabes() )));
                Gdx.app.log("DEBUG","guaranteedMorgengabe: " +guaranteedMorgengabe);
                // Save casualnumber in memory and prepare save on filesystem
                roomCasualNumber = ((CasualRoom) currentRoom).getCasualNumber();

                break;
            }
            default: {
                currentRoom = new FixedRoom(mainWorld.get(currentRoomPosInWorld), worldManager, textManager, splashManager, player, camera, assetManager, saveMap.get(currentRoomPosInWorld));
                break;
            }
        }
        Gdx.app.log("DEBUG", "ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y );
        //Keep the same state of already visited rooms
        saveMap.put(currentRoomPosInWorld,
                new RoomSaveEntry(
                        (int) finalX,
                        (int) finalY,
                        roomCasualNumber,
                        currentRoom.arePoiCleared()));


    }

    /**
     * Wraps the room contents game logic
     *
     * @param stateTime
     */
    public void doRoomContentsLogic(float stateTime) {
        currentRoom.doRoomContentsLogic(stateTime);

        //Check if all poi have been examined
        saveMap.get(currentRoomPosInWorld).poiCleared = currentRoom.arePoiCleared();

        // After room logic, handle the room change
        Vector2 playerPosition = player.getBody().getPosition();
        player.setStartX(playerPosition.x);
        player.setStartY(playerPosition.y);

        int newXPosInMatrix = (int) getCurrentRoomPosInWorld().x;
        int newYPosInMatrix = (int) getCurrentRoomPosInWorld().y;

        // Check for left or right passage
        if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY &&
                !RoomType.CEMETERY_CENTER.equals(currentRoom.getRoomType()) &&
                !RoomType.CEMETERY_TOP.equals(currentRoom.getRoomType())) {
            newXPosInMatrix--;
            player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);
        } else if ((playerPosition.x > AbstractRoom.RIGHT_BOUNDARY)) {
            newXPosInMatrix++;
            player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);
        }

        // Check for top or bottom passage
        if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY &&
                !RoomType.CEMETERY_CENTER.equals(currentRoom.getRoomType()) &&
                !RoomType.CEMETERY_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix--;
            player.setStartY(AbstractRoom.TOP_BOUNDARY - 4);
        } else if (playerPosition.y > AbstractRoom.TOP_BOUNDARY &&
                !RoomType.CHURCH_LEFT.equals(currentRoom.getRoomType()) &&
                !RoomType.CHURCH_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix++;
            player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 4);
        } else if (playerPosition.y > LHIPGame.GAME_HEIGHT*0.45 &&
                RoomType.CHURCH_ENTRANCE.equals(currentRoom.getRoomType())) {
            //ENDGAME!
            Gdx.app.exit();
        }

        // Adjustments for world extremes, semi pacman effect 
        if (((playerPosition.x < AbstractRoom.LEFT_BOUNDARY) || (playerPosition.y > AbstractRoom.TOP_BOUNDARY)) &&
                getCurrentRoomPosInWorld().x == 0 &&
                getCurrentRoomPosInWorld().y == mainWorldSize.y-1) {

            if(playerPosition.y > AbstractRoom.TOP_BOUNDARY ){
                player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 4);
            }else {
                player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);
            }

            newXPosInMatrix = (int) mainWorldSize.x-1;
            newYPosInMatrix = 0;
        } else if ( playerPosition.x < AbstractRoom.LEFT_BOUNDARY &&
                getCurrentRoomPosInWorld().x == 0 &&
                getCurrentRoomPosInWorld().y == mainWorldSize.y-2) {

            player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);

            newXPosInMatrix = (int) mainWorldSize.x-1;
            newYPosInMatrix = 1;
        } else if (((playerPosition.x > AbstractRoom.RIGHT_BOUNDARY) || (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY)) &&
                getCurrentRoomPosInWorld().x == mainWorldSize.x-1 &&
                getCurrentRoomPosInWorld().y == 0 &&
                !RoomType.START_POINT.equals(currentRoom.getRoomType())) {

            if(playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY ){
                player.setStartY(AbstractRoom.TOP_BOUNDARY - 4);
            }else {
                player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);
            }

            newXPosInMatrix = 0;
            newYPosInMatrix = (int) (mainWorldSize.y-1);
        } else if ( playerPosition.x > AbstractRoom.RIGHT_BOUNDARY &&
                getCurrentRoomPosInWorld().x == mainWorldSize.x-1 &&
                getCurrentRoomPosInWorld().y == 1) {

            player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);

            newXPosInMatrix = 0;
            newYPosInMatrix = (int) (mainWorldSize.y-2);
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
        currentRoom.drawRoomBackground();
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
}
