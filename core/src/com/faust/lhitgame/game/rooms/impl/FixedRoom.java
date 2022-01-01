package com.faust.lhitgame.game.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.faust.lhitgame.game.instances.impl.EchoActorInstance;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.rooms.AbstractRoom;
import com.faust.lhitgame.game.world.manager.WorldManager;
import com.faust.lhitgame.game.echoes.enums.EchoesActorType;
import com.faust.lhitgame.game.gameentities.enums.DecorationsEnum;
import com.faust.lhitgame.game.gameentities.enums.POIEnum;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.impl.DecorationInstance;
import com.faust.lhitgame.game.instances.impl.POIInstance;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.music.enums.TuneEnum;
import com.faust.lhitgame.game.rooms.enums.MapObjNameEnum;
import com.faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import com.faust.lhitgame.game.rooms.enums.RoomTypeEnum;
import com.faust.lhitgame.game.splash.SplashManager;
import com.faust.lhitgame.game.textbox.manager.TextBoxManager;
import com.faust.lhitgame.saves.RoomSaveEntry;
import com.faust.lhitgame.utils.DepthComparatorUtils;

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

    private static final float ECHO_ACTIVATION_DISTANCE = 40;
    private List<EchoActorInstance> echoActors; //Can have Echo actors
    private boolean echoIsActivated = false;
    private GameInstance echoTrigger;


    public FixedRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, Map<RoomFlagEnum, Boolean> roomFlags, MusicManager musicManager) {
        super(roomType, worldManager, textManager, splashManager, player, camera, assetManager, roomSaveEntry, roomFlags, musicManager);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomContent.roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // FIXME handle multiple POI
        if (Objects.nonNull(roomSaveEntry)) {
            mustClearPOI = roomSaveEntry.savedFlags.get(RoomFlagEnum.ALREADY_EXAMINED_POIS);
        }
    }

    @Override
    protected void onRoomEnter(RoomTypeEnum roomType, WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager) {
        this.echoActors = new ArrayList<>();
        mapObjects.forEach(obj -> {
            // Prepare ECHO ACTORS if not disabled
            if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && MapObjNameEnum.ECHO_ACTOR.name().equals(obj.getName())) {
                addObjAsEchoActor(obj, assetManager);
            }
        });

        worldManager.insertEchoActorsIntoWorld(echoActors);

        // FIXME handle multiple POI
        if (mustClearPOI) {
            this.roomContent.poiList.forEach(poi -> poi.setAlreadyExamined(true));
        }

        if (RoomTypeEnum.FINAL.equals(roomType)) {
            //Loop title music
            musicManager.playMusic(TuneEnum.CHURCH, 0.75f);
        } else if (roomContent.enemyList.size() > 0 || echoActors.size() > 0) {
            //Loop title music
            musicManager.playMusic(TuneEnum.DANGER, 0.75f);
        } else {
            //Loop title music
            musicManager.playMusic(TuneEnum.AMBIENCE, 0.85f);
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

        POIEnum poiType = POIEnum.valueOf((String) obj.getProperties().get("type"));
        Objects.requireNonNull(poiType);

        POIInstance instance = new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, splashManager, assetManager,
                roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_GOLDCROSS));

        roomContent.poiList.add(instance);

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

        roomContent.decorationList.add(instance);

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
                (float) obj.getProperties().get("y"), assetManager, this));

    }

    @Override
    public void drawRoomContents(SpriteBatch batch, float stateTime) {
        List<GameInstance> allInstance = new ArrayList<>();

        allInstance.addAll(roomContent.poiList);
        allInstance.addAll(roomContent.decorationList);
        allInstance.add(roomContent.player);
        allInstance.addAll(roomContent.enemyList);

        if (echoIsActivated && Objects.nonNull(echoActors)) {
            allInstance.addAll(echoActors);
        }

        // Sort by Y for depth effect. If decoration is interacted, priority is lowered
        allInstance.sort(DepthComparatorUtils::compareEntities);

        allInstance.forEach((i) -> i.draw(batch, stateTime));
//        //FIXME remove
//        if (Objects.nonNull(roomContent.roomGraph)) {
//            roomContent.roomGraph.debugDraw(cameraTemp,roomContent,batch, assetManager);
//            roomContent.enemyList.forEach(pi -> ((PathfinderInstance)pi).drawDebug(cameraTemp));
//        }

    }

    @Override
    public void dispose() {
        super.dispose();
        echoActors.forEach(EchoActorInstance::dispose);
    }

    public void doRoomContentsLogic(float stateTime) {
        super.doRoomContentsLogic(stateTime);

        // Manage echo actors
        if (echoIsActivated) {
            echoActors.forEach(actor -> {
                actor.doLogic(stateTime, roomContent);

                if (actor.hasCurrentTextBoxToShow()) {
                    this.textManager.addNewTextBox(actor.getCurrentTextBoxToShow());
                }

                if (actor.mustRemoveFromRoom()) {
                    actor.dispose();
                }
            });

            echoActors.removeIf(EchoActorInstance::mustRemoveFromRoom);
        } else {

            //activate room echo if needed
            if (Objects.nonNull(echoTrigger)) {
                echoIsActivated = roomContent.player.getBody().getPosition().dst(echoTrigger.getBody().getPosition()) <= ECHO_ACTIVATION_DISTANCE;
            }

            //Show echo text if NOW is active
            if (echoIsActivated) {
                echoActors.forEach(echoActorInstance -> {
                    echoActorInstance.playStartingSound();
                    musicManager.stopMusic();

                    if (echoActorInstance.hasCurrentTextBoxToShow()) {
                        this.textManager.addNewTextBox(echoActorInstance.getCurrentTextBoxToShow());
                    }
                });
            }
        }
    }

    @Override
    public void onRoomLeave() {
        //Disable Echo on room leave if trigger is already examined POI
        if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && Objects.nonNull(echoTrigger) &&
                (echoTrigger instanceof DecorationInstance || ((POIInstance) echoTrigger).isAlreadyExamined())) {
            roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ECHO, echoIsActivated);
        }
    }

}