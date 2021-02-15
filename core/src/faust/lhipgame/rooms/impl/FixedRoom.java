package faust.lhipgame.rooms.impl;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.echoes.EchoManager;
import faust.lhipgame.echoes.enums.EchoType;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fixes Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private RoomType roomType;
    private EchoManager echoManager;

    public FixedRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera) {
        super(roomType, worldManager, textManager,splashManager,  player, camera);
    }

    @Override
    protected void loadTiledMap(Object[] additionalLoadArguments) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    protected void initRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera) {

        switch (roomType){
            case BOAT:{
                this.echoManager = new EchoManager(EchoType.DEAD_RIVER,worldManager);
                break;
            }
            case TREE_STUMP:{
                this.echoManager = new EchoManager(EchoType.TREE_MASSACRE, worldManager);
                break;
            }
        }
    }

    @Override
    public void drawRoomContents(SpriteBatch batch, float stateTime) {
        List<GameInstance> allInstance = new ArrayList<>();

        allInstance.addAll(poiList);
        allInstance.addAll(decorationList);
        allInstance.add(player);
        allInstance.addAll(enemyList);

        if(Objects.nonNull(this.echoManager)){
            allInstance.addAll(this.echoManager.getEchoActors());
        }

        // Sort by Y for depth effect. If decoration is interacted, priority is lowered
        allInstance.sort((o1, o2) -> compareEntities(o1, o2));

        allInstance.forEach((i) -> {
            i.draw(batch, stateTime);
        });

    }

    @Override
    public void dispose() {
        super.dispose();
        if(Objects.nonNull(this.echoManager)) {
            this.echoManager.dispose();
        }
    }
}
