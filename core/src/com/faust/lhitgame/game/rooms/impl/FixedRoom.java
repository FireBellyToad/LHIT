package com.faust.lhitgame.game.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.faust.lhitgame.game.echoes.enums.EchoesActorType;
import com.faust.lhitgame.game.gameentities.enums.ItemEnum;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.impl.*;
import com.faust.lhitgame.game.instances.interfaces.Killable;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.music.enums.TuneEnum;
import com.faust.lhitgame.game.rooms.AbstractRoom;
import com.faust.lhitgame.game.rooms.areas.TriggerArea;
import com.faust.lhitgame.game.rooms.enums.*;
import com.faust.lhitgame.game.splash.SplashManager;
import com.faust.lhitgame.game.textbox.manager.TextBoxManager;
import com.faust.lhitgame.game.world.manager.WorldManager;
import com.faust.lhitgame.saves.RoomSaveEntry;
import com.faust.lhitgame.utils.DepthComparatorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Fixed Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

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

        this.roomContent.triggerAreaList = new ArrayList<>();
        mapObjects.forEach(obj -> {

            // Prepare Triggers if not disabled
            if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && MapObjTypeEnum.TRIGGER.name().equals(obj.getProperties().get("type"))) {
                addObjAsTrigger(obj);
            }
        });

        this.roomContent.echoActors = new ArrayList<>();
        mapObjects.forEach(obj -> {
            // Prepare ECHO ACTORS if not disabled
            if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && MapObjTypeEnum.ECHO_ACTOR.name().equals(obj.getProperties().get("type"))) {
                addObjAsEchoActor(obj, assetManager);
            }
        });

        worldManager.insertEchoActorsIntoWorld(roomContent.echoActors);
        worldManager.insertTriggersIntoWorld(roomContent.triggerAreaList);

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
     * Add a object as Echo Actor
     *
     * @param obj          MapObject to add
     * @param assetManager
     */
    private void addObjAsEchoActor(MapObject obj, AssetManager assetManager) {

        EchoesActorType echoesActorType = EchoesActorType.valueOf((String) obj.getProperties().get("echoesActorType"));
        int triggeredById = (int) obj.getProperties().get("triggeredById");

        TriggerArea triggerForActor = roomContent.triggerAreaList.stream().filter(t -> t.getTriggerId() == triggeredById).findFirst().orElse(null);

        roomContent.echoActors.add(new EchoActorInstance(echoesActorType,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                assetManager, triggerForActor, this));

    }

    /**
     * Add invisible emerged areas
     *
     * @param obj
     */
    protected void addObjAsTrigger(MapObject obj) {

        RectangleMapObject mapObject = (RectangleMapObject) obj;

        TriggerTypeEnum triggerTypeEnum = TriggerTypeEnum.valueOf((String) mapObject.getProperties().get("triggerType"));
        Integer referencedInstanceId = (Integer) (mapObject.getProperties().containsKey("referencedInstanceId") ? mapObject.getProperties().get("referencedInstanceId") : null);

        GameInstance referencedInstance = null;
        List<ItemEnum> requiredItemList = Collections.emptyList();

        if(Objects.nonNull(referencedInstanceId)){

            referencedInstance = roomContent.poiList.stream().filter(p -> p.getPoiIdInMap() == referencedInstanceId).findFirst().orElse(null);

            //Fallback on decorations
            if(Objects.isNull(referencedInstance)){
                referencedInstance = roomContent.decorationList.stream().filter(d -> d.getDecoIdInMap() == referencedInstanceId).findFirst().orElse(null);
            } else {
                POIInstance poi = ((POIInstance) referencedInstance);
                if(Objects.nonNull(poi.getType().getItemRequired())){
                    requiredItemList = Collections.singletonList(poi.getType().getItemRequired());
                }
            }
        }

        roomContent.triggerAreaList.add(new TriggerArea((int) mapObject.getProperties().get("id"),
                triggerTypeEnum, requiredItemList,
                mapObject.getRectangle(), referencedInstance));
    }

    @Override
    public void drawRoomContents(SpriteBatch batch, float stateTime, OrthographicCamera cameraTemp) {
        List<GameInstance> allInstance = new ArrayList<>();

        allInstance.addAll(roomContent.poiList);
        allInstance.addAll(roomContent.decorationList);
        allInstance.add(roomContent.player);
        allInstance.addAll(roomContent.enemyList);

        //add all actors with an activated trigger
        for(EchoActorInstance echoActorInstance : roomContent.echoActors){
            if(echoActorInstance.isEchoIsActive()){
                allInstance.add(echoActorInstance);
            }
        }

        allInstance.addAll(roomContent.spellEffects);

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
        roomContent.echoActors.forEach(actor -> {
            //If actor is not active
            if(!actor.isEchoIsActive()) {
                //Check if trigger is activated, then...
                if(actor.getTriggerForActor().isActivated()){
                    //activate the actor
                    actor.playStartingSound();
                    musicManager.stopMusic();

                    if (actor.hasCurrentTextBoxToShow()) {
                        this.textManager.addNewTimedTextBox(actor.getCurrentTextBoxToShow());
                    }

                } else {
                    //Nothing to do yet
                    return;
                }
            }

            //Do logic
            actor.doLogic(stateTime, roomContent);

            if (actor.hasCurrentTextBoxToShow()) {
                this.textManager.addNewTimedTextBox(actor.getCurrentTextBoxToShow());
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

    }

    @Override
    public void onRoomLeave(RoomSaveEntry roomSaveEntry) {
        roomContent.poiList.forEach(poiInstance -> roomSaveEntry.poiStates.put(poiInstance.getPoiIdInMap(), poiInstance.isAlreadyExamined()));
        roomContent.removedPoiList.forEach(poiInstance -> roomSaveEntry.poiStates.put(poiInstance.getPoiIdInMap(), poiInstance.isAlreadyExamined()));

        //Disable Echo on room leave if activated trigger is already examined POI
        if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && roomContent.triggerAreaList.stream().anyMatch(t->
                (t.isActivated() && Objects.isNull(t.getReferencedInstance())) || //activated trigger without referenced Instance
                (t.isActivated() && Objects.nonNull(t.getReferencedInstance()) && !(t.getReferencedInstance() instanceof POIInstance)) ||  //activated trigger with a non POI referenced Instance
                (t.isActivated() && Objects.nonNull(t.getReferencedInstance()) && t.getReferencedInstance() instanceof POIInstance && ((POIInstance) t.getReferencedInstance()).isAlreadyExamined()))) {//activated trigger with an activated POI referenced Instance
            roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ECHO, true);
        }

        //always enable enemies
        roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ENEMIES, false);
    }

}
