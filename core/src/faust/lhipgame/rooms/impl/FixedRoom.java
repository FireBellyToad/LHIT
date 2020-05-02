package faust.lhipgame.rooms.impl;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

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
