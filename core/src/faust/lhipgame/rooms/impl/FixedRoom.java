package faust.lhipgame.rooms.impl;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.echoes.enums.EchoesActorType;
import faust.lhipgame.gameentities.enums.DecorationsEnum;
import faust.lhipgame.gameentities.enums.POIEnum;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.impl.DecorationInstance;
import faust.lhipgame.instances.impl.EchoActorInstance;
import faust.lhipgame.instances.impl.POIInstance;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.AbstractRoom;
import faust.lhipgame.rooms.RoomSaveEntry;
import faust.lhipgame.rooms.enums.MapObjNameEnum;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Fixed Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private static final float ECHO_ACTIVATION_DISTANCE = 40;
    private List<EchoActorInstance> echoActors; //Can have Echo actors
    private boolean echoIsActivated = false;
    private GameInstance echoTrigger;

    public FixedRoom( final RoomType roomType, final WorldManager worldManager, final TextManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final RoomSaveEntry roomSaveEntry) {
        super(roomType, worldManager, textManager, splashManager, player, camera, roomSaveEntry);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // FIXME handle multiple POI
        if (Objects.nonNull(roomSaveEntry)) {
            mustClearPOI = roomSaveEntry.poiCleared;
        }
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

        // FIXME handle multiple POI
        if(mustClearPOI){
            this.poiList.forEach(poi -> poi.setAlreadyExamined(true));
        }

    }
    /**
     * Add a object as POI, with custom echo logic
     *
     * @param obj
     * @param textManager
     */
    @Override
    protected void addObjAsPOI(MapObject obj, TextManager textManager) {

        POIEnum poiType = POIEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(poiType);

        POIInstance instance = new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, player, splashManager);

        poiList.add(instance);

        //Check if is Echo trigger
        if(Objects.nonNull(obj.getProperties().get("isEchoTrigger"))){

            if(Objects.nonNull(echoTrigger)){
                throw new RuntimeException("More than one echo trigger in the room!");
            }

            echoTrigger = instance;
        }
    }

    ;

    /**
     * Add a object as Decoration, with custom echo logic
     *
     * @param obj MapObject to add
     */
    @Override
    protected void addObjAsDecoration(MapObject obj) {

        DecorationsEnum decoType = DecorationsEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(decoType);

        DecorationInstance instance = new DecorationInstance(
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                decoType);

        decorationList.add(instance);

        //Check if is Echo trigger
        if(Objects.nonNull(obj.getProperties().get("isEchoTrigger"))){

            if(Objects.nonNull(echoTrigger)){
                throw new RuntimeException("More than one echo trigger in the room!");
            }

            echoTrigger = instance;
        }
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

        if (echoIsActivated && Objects.nonNull(echoActors)) {
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
        if(echoIsActivated){
            echoActors.forEach(actor -> {
                actor.doLogic(stateTime);

                if (actor.mustRemoveFromRoom()) {
                    actor.dispose();
                }
            });

            echoActors.removeIf(actor -> actor.mustRemoveFromRoom());
        } else {

            //activate room echo if needed
            if(Objects.nonNull(echoTrigger)) {
                echoIsActivated = player.getBody().getPosition().dst(echoTrigger.getBody().getPosition()) <= ECHO_ACTIVATION_DISTANCE;
            }

            //Show echo text if NOW is active
            if(echoIsActivated){
                echoActors.forEach( echoActorInstance -> {
                    if(Objects.nonNull(echoActorInstance.getCurrentTextBoxToShow())){
                        this.textManager.addNewTextBox(echoActorInstance.getCurrentTextBoxToShow());
                    }
                });
            }
        }
    }

}
