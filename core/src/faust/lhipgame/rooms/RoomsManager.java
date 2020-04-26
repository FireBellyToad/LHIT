package faust.lhipgame.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.*;

public class RoomsManager {

    private AbstractRoom currentRoom;
    private final Vector2 currentRoomPosInWorld = new Vector2(0, 0);

    /**
     * MainWorld Matrix
     */
    private final Map<Vector2, RoomType> mainWorld = new HashMap<>();
    private final Map<Vector2, Integer> mainWorldPredefinedCasualNumbers = new HashMap<>();
    private final Vector2 mainWorldSize = new Vector2(0, 0);
    private final List<CasualRoomNumberSaveEntry> saveList = new ArrayList<>();

    private WorldManager worldManager;
    private TextManager textManager;
    private PlayerInstance player;
    private OrthographicCamera camera;

    public RoomsManager(WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        this.worldManager = worldManager;
        this.textManager = textManager;
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
        try{
            JsonValue numbers = new JsonReader().parse(Gdx.files.local("mainWorldSave.json"));

            numbers.forEach((t) -> {
                Vector2 v = new Vector2(t.getFloat("x"), t.getFloat("y"));
                int casualNumberPredefined = t.getInt("casualNumber");
                Objects.requireNonNull(casualNumberPredefined);
                mainWorldPredefinedCasualNumbers.put(v, casualNumberPredefined);
            });

        }catch (SerializationException ex){
            Gdx.app.log("WARN","No valid savefile to load");
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

        switch (mainWorld.get(currentRoomPosInWorld)) {
            case CASUAL: {

                currentRoom = new CasualRoom(worldManager, textManager, player, camera, mainWorldPredefinedCasualNumbers.get(currentRoomPosInWorld));
                // TODO RIMUOVERE
                textManager.addNewTextBox("ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y);

                final int roomCasualNumber = ((CasualRoom) currentRoom).getCasualNumber();

                // Save casualnumber in memory and prepare save on filesystem
                saveList.add(new CasualRoomNumberSaveEntry(
                        (int) finalX,
                        (int) finalY,
                        roomCasualNumber));

                mainWorldPredefinedCasualNumbers.put(currentRoomPosInWorld,roomCasualNumber);
                break;
            }
            default: {
                currentRoom = new FixedRoom(mainWorld.get(currentRoomPosInWorld), worldManager, textManager, player, camera);
                // TODO RIMUOVERE
                textManager.addNewTextBox("ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y);

                break;
            }
        }

    }

    /**
     * Wraps the room contents game logic
     */
    public void doRoomContentsLogic() {
        currentRoom.doRoomContentsLogic();

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
            player.setStartX(LHIPGame.GAME_WIDTH - 16);
        } else if (playerPosition.x > AbstractRoom.RIGHT_BOUNDARY) {
            newXPosInMatrix++;
            player.setStartX(0);
        }

        // Check for top or bottom passage
        if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY &&
                !RoomType.CEMETERY_CENTER.equals(currentRoom.getRoomType()) &&
                !RoomType.CEMETERY_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix--;
            player.setStartY(LHIPGame.GAME_HEIGHT - 16);
        } else if (playerPosition.y > AbstractRoom.TOP_BOUNDARY &&
                !RoomType.CHURCH_LEFT.equals(currentRoom.getRoomType()) &&
                !RoomType.CHURCH_ENTRANCE.equals(currentRoom.getRoomType()) &&
                !RoomType.CHURCH_RIGHT.equals(currentRoom.getRoomType())) {
            newYPosInMatrix++;
            player.setStartY(0);
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
        String saveFile = json.toJson(saveList);
        Gdx.files.local("saves/mainWorldSave.json").writeString(saveFile, false);
    }
}
