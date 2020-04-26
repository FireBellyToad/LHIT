package faust.lhipgame.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.Objects;

/**
 * Fixes Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private RoomType roomType;

    public FixedRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        super(roomType, worldManager, textManager, player, camera);

    }

    @Override
    protected void loadTiledMap(Object[] additionalLoadArguments) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    protected void initRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        // Nothing to do... yet
    }
}
