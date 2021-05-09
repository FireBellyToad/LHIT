package faust.lhipgame.game.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhipgame.game.echoes.enums.EchoesActorType;
import faust.lhipgame.game.gameentities.enums.DecorationsEnum;
import faust.lhipgame.game.gameentities.enums.POIEnum;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.impl.DecorationInstance;
import faust.lhipgame.game.instances.impl.EchoActorInstance;
import faust.lhipgame.game.instances.impl.POIInstance;
import faust.lhipgame.game.instances.impl.PlayerInstance;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.rooms.AbstractRoom;
import faust.lhipgame.game.rooms.enums.MapObjNameEnum;
import faust.lhipgame.game.rooms.enums.RoomFlagEnum;
import faust.lhipgame.game.rooms.enums.RoomTypeEnum;
import faust.lhipgame.game.splash.SplashManager;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.game.world.manager.WorldManager;
import faust.lhipgame.saves.RoomSaveEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fixed Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private static final float ECHO_ACTIVATION_DISTANCE = 35;
    private List<EchoActorInstance> echoActors; //Can have Echo actors
    private boolean echoIsActivated = false;
    private GameInstance echoTrigger;

    public FixedRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, Map<RoomFlagEnum, Boolean> roomFlags, MusicManager musicManager) {
        super(roomType, worldManager, textManager, splashManager, player, camera, assetManager, roomSaveEntry, roomFlags, musicManager);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // FIXME handle multiple POI
        if (Objects.nonNull(roomSaveEntry)) {
            mustClearPOI = roomSaveEntry.savedFlags.get(RoomFlagEnum.ALREADY_EXAMINED_POIS);
        }
    }

    @Override
    protected void initRoom(RoomTypeEnum roomType, WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager) {
        this.echoActors = new ArrayList<>();
        mapObjects.forEach(obj -> {
            // Prepare ECHO ACTORS if not disabled
            if (!roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && MapObjNameEnum.ECHO_ACTOR.name().equals(obj.getName())) {
                addObjAsEchoActor(obj, assetManager);
            }
        });

        worldManager.insertEchoActorsIntoWorld(echoActors);

        // FIXME handle multiple POI
        if (mustClearPOI) {
            this.poiList.forEach(poi -> poi.setAlreadyExamined(true));
        }

        if (enemyList.size() > 0 || echoActors.size() > 0) {
            //Loop title music
            musicManager.playMusic(TuneEnum.DANGER, 0.75f);
        } else {
            //Loop title music
            musicManager.playMusic(TuneEnum.AMBIENCE, 0.75f);
        }
    }

    /**
     * Add a object as POI, with custom echo logic
     *
     * @param obj
     * @param textManager
     */
    @Override
    protected void addObjAsPOI(MapObject obj, TextBoxManager textManager, AssetManager assetManager) {

        POIEnum poiType = POIEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(poiType);

        POIInstance instance = new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, player, splashManager, assetManager,
                roomFlags.get(RoomFlagEnum.GUARANTEED_MORGENGABE));

        poiList.add(instance);

        //Check if is Echo trigger
        if (Objects.nonNull(obj.getProperties().get("isEchoTrigger"))) {

            if (Objects.nonNull(echoTrigger)) {
                throw new RuntimeException("More than one echo trigger in the room!");
            }

            echoTrigger = instance;
        }
    }

    /**
     * Add a object as Decoration, with custom echo logic
     *
     * @param obj MapObject to add
     */
    @Override
    protected void addObjAsDecoration(MapObject obj, AssetManager assetManager) {

        DecorationsEnum decoType = DecorationsEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(decoType);

        DecorationInstance instance = new DecorationInstance(
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                decoType, assetManager);

        decorationList.add(instance);

        //Check if is Echo trigger
        if (Objects.nonNull(obj.getProperties().get("isEchoTrigger"))) {

            if (Objects.nonNull(echoTrigger)) {
                throw new RuntimeException("More than one echo trigger in the room!");
            }

            echoTrigger = instance;
        }
    }

    /**
     * Add a object as Echo Actor
     *
     * @param obj          MapObject to add
     * @param assetManager
     */
    private void addObjAsEchoActor(MapObject obj, AssetManager assetManager) {

        EchoesActorType echoesActorType = EchoesActorType.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(echoesActorType);

        echoActors.add(new EchoActorInstance(echoesActorType,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"), assetManager));

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

        //Disable all echoes if there are no echoactor
        if(!roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && echoActors.size() == 0){
            roomFlags.put(RoomFlagEnum.DISABLED_ECHO, true);
        }

        // Manage echo actors
        if (echoIsActivated) {
            echoActors.forEach(actor -> {
                actor.doLogic(stateTime);

                if (actor.hasCurrentTextBoxToShow()) {
                    this.textManager.addNewTextBox(actor.getCurrentTextBoxToShow());
                }

                if (actor.mustRemoveFromRoom()) {
                    actor.dispose();
                }
            });

            echoActors.removeIf(actor -> actor.mustRemoveFromRoom());
        } else {

            //activate room echo if needed
            if (Objects.nonNull(echoTrigger)) {
                echoIsActivated = player.getBody().getPosition().dst(echoTrigger.getBody().getPosition()) <= ECHO_ACTIVATION_DISTANCE;
            }

            //Show echo text if NOW is active
            if (echoIsActivated) {
                echoActors.forEach(echoActorInstance -> {
                    echoActorInstance.playStartingSound();

                    if (echoActorInstance.hasCurrentTextBoxToShow()) {
                        this.textManager.addNewTextBox(echoActorInstance.getCurrentTextBoxToShow());
                    }
                });
            }
        }
    }

}
