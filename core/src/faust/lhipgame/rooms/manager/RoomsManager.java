package faust.lhipgame.rooms.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.RoomNumberSaveEntry;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.rooms.impl.CasualRoom;
import faust.lhipgame.rooms.impl.FixedRoom;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

import java.util.*;

public class RoomsManager {

    private final SplashManager splashManager;
    private AbstractRoom currentRoom;
    private final Vector2 currentRoomPosInWorld = new Vector2(0, 0);

    /**
     * MainWorld Matrix
     */
    private final Map<Vector2, RoomType> mainWorld = new HashMap<>();
    private final Map<Vector2,RoomNumberSaveEntry> saveMap = new HashMap<>();
    private final Vector2 mainWorldSize = new Vector2(0, 0);

    private WorldManager worldManager;
    private TextManager textManager;
    private PlayerInstance player;
    private OrthographicCamera camera;

    public RoomsManager(WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera) {
        this.worldManager = worldManager;
        this.textManager = textManager;
        this.splashManager = splashManager;
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

        loadPredefinedCasualRoomNumbers();
    }

    /**
     * Load predefined casual room numbers
     */
    private void loadPredefinedCasualRoomNumbers() {
        //Try to load predefined casualnumbers for casual rooms from file
        try {
            JsonValue numbers = new JsonReader().parse(Gdx.files.local("saves/mainWorldSave.json"));

            if(Objects.isNull(numbers)){
                return;
            }

            numbers.forEach((t) -> {
                Vector2 v = new Vector2(t.getFloat("x"), t.getFloat("y"));
                int casualNumberPredefined = t.getInt("casualNumber");
                boolean arePoiCleared = t.getBoolean("poiCleared");
                Objects.requireNonNull(casualNumberPredefined);

                saveMap.put(v, new RoomNumberSaveEntry(
                        (int) v.x, (int) v.y, casualNumberPredefined,
                        arePoiCleared));
            });

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
            saveMap.get(currentRoomPosInWorld).poiCleared = currentRoom.arePoiCleared();
            currentRoom.dispose();
        }


        float finalX = (newRoomPosX < 0 ? mainWorldSize.x - 1 : (newRoomPosX == mainWorldSize.x ? 0 : newRoomPosX));
        float finalY = (newRoomPosY < 0 ? mainWorldSize.y - 1 : (newRoomPosY == mainWorldSize.y ? 0 : newRoomPosY));

        currentRoomPosInWorld.set(finalX, finalY);

        int roomCasualNumber = 0;
        switch (mainWorld.get(currentRoomPosInWorld)) {
            case CASUAL: {

                currentRoom = new CasualRoom(worldManager, textManager, splashManager, player, camera, saveMap.get(currentRoomPosInWorld));

                // Save casualnumber in memory and prepare save on filesystem
                roomCasualNumber = ((CasualRoom) currentRoom).getCasualNumber();
                Gdx.app.log("DEBUG", "ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y );
                break;
            }
            default: {
                currentRoom = new FixedRoom(mainWorld.get(currentRoomPosInWorld), worldManager, textManager, splashManager, player, camera);
                break;
            }
        }
        //Keep the same state of already visited rooms
        saveMap.put(currentRoomPosInWorld,
                new RoomNumberSaveEntry(
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
                !RoomType.CHURCH_ENTRANCE.equals(currentRoom.getRoomType()) &&
                !RoomType.CHURCH_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix++;
            player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 4);
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
                getCurrentRoomPosInWorld().y == 0) {

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

        if (getCurrentRoomPosInWorld().x != newXPosInMatrix || getCurrentRoomPosInWorld().y != newYPosInMatrix) {
            changeCurrentRoom(newXPosInMatrix, newYPosInMatrix);
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
        saveOnFile();

        currentRoom.dispose();
    }

    /**
     * Save on filesystem the predefined numbers of the casual rooms
     */
    private void saveOnFile() {

        //TODO per nome della partita
        Json json = new Json();
        String saveFile = json.toJson(saveMap.values());
        Gdx.files.local("saves/mainWorldSave.json").writeString(saveFile, false);
    }
}
