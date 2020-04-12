package faust.lhipgame.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

/**
 * Casual Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private RoomType roomType;

    public FixedRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        super(roomType, worldManager, textManager, player, camera);

    }

    @Override
    protected void initRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        // Nothing to do... yet
    }
}
