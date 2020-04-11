package faust.lhipgame.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.DecorationInstance;
import faust.lhipgame.instances.POIInstance;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.List;
import java.util.Objects;

/**
 * Abstract room common logic
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AbstractRoom {

    /**
     * Boundaries for room changing
     */
    public static final float LEFT_BOUNDARY = 0;
    public static final float BOTTOM_BOUNDARY = 0;
    public static final float RIGHT_BOUNDARY = LHIPGame.GAME_WIDTH - 32;
    public static final float TOP_BOUNDARY = LHIPGame.GAME_HEIGHT - 32;

    protected TiledMap tiledMap;
    protected TiledMapRenderer tiledMapRenderer;
    protected MapObjects mapObjects;

    protected List<POIInstance> poiList;
    protected List<DecorationInstance> decorationList;
    protected PlayerInstance player;

    public AbstractRoom(final WorldManager worldManager, final TextManager textManager, final PlayerInstance player, final OrthographicCamera camera) {
        Objects.requireNonNull(worldManager);
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(player);

        this.initRoom(worldManager, textManager, player,camera);
    }

    /**
     * Method for room initialization
     * @param worldManager
     * @param textManager
     * @param player
     * @param camera
     */
    protected abstract void initRoom(final WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera);

    /**
     * Draws room background
     */
    public void drawRoomBackground(){
        tiledMapRenderer.render();
    }

    /**
     * Draws room contents
     * @param batch
     * @param stateTime
     */
    public void drawRoomContents(final SpriteBatch batch, float stateTime) {

        poiList.forEach((poi) -> poi.draw(batch, stateTime));

        decorationList.forEach((deco) -> {
            if (deco.getBody().getPosition().y >= player.getBody().getPosition().y - 4)
                deco.draw(batch, stateTime);
        });

        player.draw(batch, stateTime);

        decorationList.forEach((deco) -> {
            if (deco.getBody().getPosition().y < player.getBody().getPosition().y - 4)
                deco.draw(batch, stateTime);
        });

    }

    /**
     * Disposes the terrain and the contents of the room
     */
    public void dispose(){
        tiledMap.dispose();
        decorationList.forEach((deco) -> deco.dispose());
        poiList.forEach((poi) -> poi.dispose());
    }
}
