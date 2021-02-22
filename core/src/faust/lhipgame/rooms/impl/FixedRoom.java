package faust.lhipgame.rooms.impl;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.echoes.enums.EchoesActorType;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.impl.EchoActorInstance;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.enums.MapObjNameEnum;
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

    private List<EchoActorInstance> echoActors;

    public FixedRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera) {
        super(roomType, worldManager, textManager, splashManager, player, camera);
    }

    @Override
    protected void loadTiledMap(Object[] additionalLoadArguments) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

    }

    @Override
    protected void initRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera) {
        this.echoActors = new ArrayList<>();
        mapObjects.forEach(obj -> {
            // Prepare ECHO ACTORS
            if (MapObjNameEnum.ECHO_ACTOR.name().equals(obj.getName())) {
                addObjAsEchoActor(obj);
            }
        });

        worldManager.insertEchoActorsIntoWorld(echoActors);

    }

    /**
     * Add a object as Echo Actor
     *
     * @param obj MapObject to add
     */
    private void addObjAsEchoActor(MapObject obj) {

        EchoesActorType echoesActorType = EchoesActorType.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(echoesActorType);

        echoActors.add(new EchoActorInstance(echoesActorType,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y")));

    }

    @Override
    public void drawRoomContents(SpriteBatch batch, float stateTime) {
        List<GameInstance> allInstance = new ArrayList<>();

        allInstance.addAll(poiList);
        allInstance.addAll(decorationList);
        allInstance.add(player);
        allInstance.addAll(enemyList);

        if (Objects.nonNull(echoActors)) {
            allInstance.addAll(echoActors);
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
        echoActors.forEach(echoActorInstance -> echoActorInstance.dispose());
    }

    public void doRoomContentsLogic(float stateTime) {
        super.doRoomContentsLogic(stateTime);

        // Manage echo actors
        echoActors.forEach(actor -> {
            if (actor.mustRemoveFromRoom()) {
                actor.dispose();
            } else {
                actor.doLogic(stateTime);
            }
        });

        echoActors.removeIf(actor -> actor.mustRemoveFromRoom());

    }
}
