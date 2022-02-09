package com.faust.lhitgame.game.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.faust.lhitgame.game.echoes.enums.EchoesActorType;
import com.faust.lhitgame.game.gameentities.enums.DecorationsEnum;
import com.faust.lhitgame.game.gameentities.enums.POIEnum;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.impl.*;
import com.faust.lhitgame.game.instances.interfaces.Killable;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.music.enums.TuneEnum;
import com.faust.lhitgame.game.rooms.AbstractRoom;
import com.faust.lhitgame.game.rooms.enums.MapLayersEnum;
import com.faust.lhitgame.game.rooms.enums.MapObjNameEnum;
import com.faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import com.faust.lhitgame.game.rooms.enums.RoomTypeEnum;
import com.faust.lhitgame.game.splash.SplashManager;
import com.faust.lhitgame.game.textbox.manager.TextBoxManager;
import com.faust.lhitgame.game.world.manager.WorldManager;
import com.faust.lhitgame.saves.RoomSaveEntry;
import com.faust.lhitgame.utils.DepthComparatorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fixed Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private static final float ECHO_ACTIVATION_DISTANCE = 40;
    private boolean echoIsActivated = false;
    private GameInstance echoTrigger;
    private String layerToDraw = MapLayersEnum.TERRAIN_LAYER.getLayerName();


    public FixedRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, MusicManager musicManager) {
        super(roomType, worldManager, textManager, splashManager, player, camera, assetManager, roomSaveEntry, musicManager);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {
        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomContent.roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

    }

    @Override
    protected void onRoomEnter(RoomTypeEnum roomType, WorldManager worldManager, AssetManager assetManager, RoomSaveEntry roomSaveEntry) {
        this.roomContent.echoActors = new ArrayList<>();
        mapObjects.forEach(obj -> {
            // Prepare ECHO ACTORS if not disabled
            if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && MapObjNameEnum.ECHO_ACTOR.name().equals(obj.getName())) {
                addObjAsEchoActor(obj, assetManager);
            }
        });

        worldManager.insertEchoActorsIntoWorld(roomContent.echoActors);

        if (Objects.nonNull(roomSaveEntry)) {
            roomSaveEntry.poiStates.forEach((id, isExamined) -> {
                //update POI status
                POIInstance poi = this.roomContent.poiList.stream().filter(p -> id.equals(p.getPoiIdInMap())).findFirst().orElse(null);

                if (Objects.nonNull(poi)) {
                    poi.setAlreadyExamined(isExamined);
                }
            });
        }

        if (RoomTypeEnum.FINAL.equals(roomType)) {
            //Loop title music
            musicManager.playMusic(TuneEnum.CHURCH, 0.75f);
        } else if (roomContent.enemyList.size() > 0 || roomContent.echoActors.size() > 0) {
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
                poiType, (int) obj.getProperties().get("id"), splashManager, assetManager,
                roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_GOLDCROSS));

        roomContent.poiList.add(instance);

        //Check if is Echo trigger
        if (Objects.nonNull(obj.getProperties().get("isEchoTrigger"))) {

            if (Objects.nonNull(echoTrigger)) {
                throw new RuntimeException("More than one echo trigger in the room!");
            }

            echoTrigger = instance;
            if (Objects.nonNull(obj.getProperties().get("mustTriggerAfterExamination"))) {
                ((POIInstance) echoTrigger).setMustTriggerAfterExamination(obj.getProperties().get("mustTriggerAfterExamination", Boolean.class));
            }
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

        roomContent.echoActors.add(new EchoActorInstance(echoesActorType,
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

        if (echoIsActivated && Objects.nonNull(roomContent.echoActors)) {
            allInstance.addAll(roomContent.echoActors);
        }

        // Sort by Y for depth effect. If decoration is interacted, priority is lowered
        allInstance.sort(DepthComparatorUtils::compareEntities);

        allInstance.forEach((i) -> i.draw(batch, stateTime));

    }

    @Override
    public void drawRoomTerrain() {
        MapLayers mapLayers = tiledMap.getLayers();

        TiledMapTileLayer terrainLayer = (TiledMapTileLayer) mapLayers.get(layerToDraw);

        //Overlay layer should is required
        Objects.requireNonNull(terrainLayer);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(terrainLayer);
        tiledMapRenderer.getBatch().end();
    }

    @Override
    public void dispose() {
        super.dispose();
        roomContent.echoActors.forEach(EchoActorInstance::dispose);
    }

    public void doRoomContentsLogic(float stateTime) {
        super.doRoomContentsLogic(stateTime);
        layerToDraw = MapLayersEnum.TERRAIN_LAYER.getLayerName();
        roomContent.enemyList.removeIf(ene -> ene instanceof EscapePortalInstance && ((Killable) ene).isDead());

        // Manage echo actors
        if (echoIsActivated) {
            roomContent.echoActors.forEach(actor -> {
                actor.doLogic(stateTime, roomContent);

                if (actor.hasCurrentTextBoxToShow()) {
                    this.textManager.addNewTextBox(actor.getCurrentTextBoxToShow());
                }

                if (actor.mustRemoveFromRoom()) {
                    actor.dispose();
                }

                //change only if is default
                if (MapLayersEnum.TERRAIN_LAYER.getLayerName().equals(layerToDraw)) {
                    layerToDraw = actor.overrideMapLayerDrawn();
                }
            });

            roomContent.echoActors.removeIf(EchoActorInstance::mustRemoveFromRoom);
        } else {

            //activate room echo if needed. If mustTriggerAfterExamination then wait for activation
            if (Objects.nonNull(echoTrigger) &&
                    (!((POIInstance) echoTrigger).mustTriggerAfterExamination() || ((POIInstance) echoTrigger).isAlreadyExamined())) {
                echoIsActivated = roomContent.player.getBody().getPosition().dst(echoTrigger.getBody().getPosition()) <= ECHO_ACTIVATION_DISTANCE;
            }

            //Show echo text if NOW is active
            if (echoIsActivated) {
                roomContent.echoActors.forEach(echoActorInstance -> {
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
    public void onRoomLeave(RoomSaveEntry roomSaveEntry) {
        roomContent.poiList.forEach(poiInstance -> {
            roomSaveEntry.poiStates.put(poiInstance.getPoiIdInMap(), poiInstance.isAlreadyExamined());
        });

        //Disable Echo on room leave if trigger is already examined POI
        if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && Objects.nonNull(echoTrigger) &&
                (echoTrigger instanceof DecorationInstance || ((POIInstance) echoTrigger).isAlreadyExamined())) {
            roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ECHO, echoIsActivated);
        }

        //always enable enemies
        roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ENEMIES, false);
    }

}
