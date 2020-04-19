package faust.lhipgame.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RoomsManager {

    private AbstractRoom currentRoom;
    private final Vector2 currentRoomPosInWorld = new Vector2(0, 0);

    /**
     * MainWorld Matrix
     */
    private final Map<Vector2,RoomType> mainWorld = new HashMap<>();
    private final Vector2 mainWorldSize = new Vector2(0,0);

    private WorldManager worldManager;
    private TextManager textManager;
    private PlayerInstance player;
    private OrthographicCamera camera;

    public RoomsManager(WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        this.worldManager = worldManager;
        this.textManager = textManager;
        this.player = player;
        this.camera = camera;

        initMap();
        changeCurrentRoom(2, 0);
    }

    /**
     * Inits world from file
     */
    private void initMap() {

        JsonValue terrains = new JsonReader().parse(Gdx.files.internal("mainWorld.json")).get("terrains");
        mainWorldSize.set(0,0);

        terrains.forEach((t) -> {
            Vector2 v = new Vector2( t.getFloat("x"),  t.getFloat("y"));
            RoomType type = RoomType.getFromString(t.getString("type"));
            Objects.requireNonNull(type);
            mainWorld.put(v,type);
            mainWorldSize.set(Math.max(mainWorldSize.x,v.x),Math.max(mainWorldSize.y,v.y));
        });
        // Finalize size
        mainWorldSize.set(mainWorldSize.x+1,mainWorldSize.y+1);


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

        //TODO CAMBIA
        float finalX = (newRoomPosX < 0 ? mainWorldSize.x- 1 : (newRoomPosX == mainWorldSize.x ? 0 : newRoomPosX));
        float finalY = (newRoomPosY < 0 ? mainWorldSize.y - 1 : (newRoomPosY == mainWorldSize.y ? 0 : newRoomPosY));

        currentRoomPosInWorld.set(finalX, finalY);

        switch (mainWorld.get(currentRoomPosInWorld)) {
            case CASUAL: {
                currentRoom = new CasualRoom(worldManager, textManager, player, camera);
                // TODO RIMUOVERE
                textManager.addNewTextBox("ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y);
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

    public void doLogic() {
        // Do Player logic
        player.doLogic();

        // After Player logic, handle the roomm
        Vector2 playerPosition = player.getBody().getPosition();
        player.setStartX(playerPosition.x);
        player.setStartY(playerPosition.y);

        int newXPosInMatrix = (int) getCurrentRoomPosInWorld().x;
        int newYPosInMatrix = (int) getCurrentRoomPosInWorld().y;

        if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY) {
            newXPosInMatrix--;
            player.setStartX(LHIPGame.GAME_WIDTH - 16);
        } else if (playerPosition.x > AbstractRoom.RIGHT_BOUNDARY) {
            newXPosInMatrix++;
            player.setStartX(0);
        }

        if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY) {
            newYPosInMatrix--;
            player.setStartY(LHIPGame.GAME_HEIGHT - 16);
        } else if (playerPosition.y > AbstractRoom.TOP_BOUNDARY) {
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
        currentRoom.dispose();
    }
}
