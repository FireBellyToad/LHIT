package faust.lhipgame.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.Objects;

public class RoomsManager {

    private AbstractRoom currentRoom;
    private final Vector2 currentRoomPosInWorld = new Vector2(0, 0);

    /**
     * MainWorld Matrix
     */
    private RoomType mainWorld[][] = {
            {RoomType.CASUAL, RoomType.CASUAL},
            {RoomType.CASUAL, RoomType.CASUAL},
    };

    private WorldManager worldManager;
    private TextManager textManager;
    private PlayerInstance player;
    private OrthographicCamera camera;

    public RoomsManager(WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        this.worldManager = worldManager;
        this.textManager = textManager;
        this.player = player;
        this.camera = camera;

        changeCurrentRoom(0, 0);
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
        int finalX = (newRoomPosX < 0 ? mainWorld[0].length-1 : (newRoomPosX == mainWorld[0].length ? 0 : newRoomPosX));
        int finalY = (newRoomPosY < 0 ? mainWorld.length-1  : (newRoomPosY == mainWorld.length  ? 0 : newRoomPosY));

        currentRoomPosInWorld.set(finalX, finalY);

        if (RoomType.CASUAL.equals(mainWorld[finalX][finalY])) {
            currentRoom = new CasualRoom(worldManager, textManager, player, camera);
            // TODO RIMUOVERE
            textManager.addNewTextBox("ROOM " + (int) currentRoomPosInWorld.x + "," + (int) currentRoomPosInWorld.y);
        }

    }

    public void doLogic() {
        Vector2 playerPosition = player.getBody().getPosition();
        float newPlayerX = playerPosition.x;
        float newPlayerY = playerPosition.y;

        int newXPosInMatrix = (int) getCurrentRoomPosInWorld().x;
        int newYPosInMatrix = (int) getCurrentRoomPosInWorld().y;

        if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY) {
            newXPosInMatrix--;
            newPlayerX = LHIPGame.GAME_WIDTH - 32;
        } else if (playerPosition.x > AbstractRoom.RIGHT_BOUNDARY) {
            newXPosInMatrix++;
            newPlayerX = 0;
        }

        if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY) {
            newYPosInMatrix--;
            newPlayerY = LHIPGame.GAME_HEIGHT - 32;
        } else if (playerPosition.y > AbstractRoom.TOP_BOUNDARY) {
            newYPosInMatrix++;
            newPlayerY = 0;
        }

        if (getCurrentRoomPosInWorld().x != newXPosInMatrix || getCurrentRoomPosInWorld().y != newYPosInMatrix) {
            player.getBody().setTransform(newPlayerX,newPlayerY,0);
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
