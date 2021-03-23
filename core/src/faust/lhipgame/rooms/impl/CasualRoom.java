package faust.lhipgame.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.RoomSaveEntry;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

import java.util.Objects;

/**
 * Casual Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CasualRoom extends AbstractRoom {

    public static final int CASUAL_TOTAL = 6;
    private int casualNumber;

    public CasualRoom(WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager, RoomSaveEntry roomSaveEntry, boolean guaranteedMorgengabe) {
        super(RoomType.CASUAL, worldManager, textManager, splashManager, player, camera, assetManager, roomSaveEntry, guaranteedMorgengabe);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {

        // If has a predefined casual number (like from a savefile or because it was already visited) use that one
        // Or else generate a new number.
        if (Objects.nonNull(roomSaveEntry)) {
            casualNumber = roomSaveEntry.casualNumber;

            // FIXME handle multiple POI
            mustClearPOI = roomSaveEntry.poiCleared;

        } else {
            if(!guaranteedMorgengabe){
                casualNumber = MathUtils.random(1, CasualRoom.CASUAL_TOTAL);
            } else {
                //pick only ones with skeleton poi
                casualNumber = MathUtils.randomBoolean() ? 1 : MathUtils.randomBoolean() ? 6 :5;
            }
        }
        //Enforce number between 1 and CASUAL_TOTAL. Seemingly unnecessary, but...
        casualNumber = MathUtils.clamp(casualNumber, 1,CasualRoom.CASUAL_TOTAL);

        // Casual maps range from casual1.tmx to casual6.tmx, with a %d to be mapped
        roomFileName = String.format(roomFileName, casualNumber);

        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);


    }

    @Override
    protected void initRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager) {
        // FIXME handle multiple POI
        if(mustClearPOI){
            this.poiList.forEach(poi -> poi.setAlreadyExamined(true));
        }
    }

    public int getCasualNumber() {
        return casualNumber;
    }
}
