package faust.lhipgame.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.instances.DecorationInstance;
import faust.lhipgame.instances.POIInstance;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.MapLayersEnum;
import faust.lhipgame.rooms.enums.MapObjNameEnum;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.ArrayList;

/**
 * Casual Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CasualRoom extends AbstractRoom {

    public CasualRoom(WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {
        super(worldManager, textManager, player, camera);
    }

    @Override
    protected void initRoom(WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {

        //TODO Randomize terrain
        // Load Tiled map
        tiledMap = new TmxMapLoader().load("terrains/test.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 0.90f);

        mapObjects = tiledMap.getLayers().get(MapLayersEnum.OBJECT_LAYER.ordinal()).getObjects();

        // Set camera for rendering
        tiledMapRenderer.setView(camera);

        this.player = player;

        poiList = new ArrayList<POIInstance>();
        decorationList = new ArrayList<DecorationInstance>();

        for (MapObject obj : mapObjects) {

            if (MapObjNameEnum.POI.name().equals(obj.getName())) {
                poiList.add(new POIInstance(textManager,
                        ((float) obj.getProperties().get("x")),
                        ((float) obj.getProperties().get("y"))));
            }


            if (MapObjNameEnum.DECO.name().equals(obj.getName())) {
                decorationList.add(new DecorationInstance(
                        ((float) obj.getProperties().get("x")),
                        ((float) obj.getProperties().get("y"))));
            }

        }

        worldManager.insertPOIIntoWorld(poiList);
        worldManager.insertDecorationsIntoWorld(decorationList);
        player.changePOIList(poiList);
    }
}
