package faust.lhitgame.game.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import faust.lhitgame.LHITGame;
import faust.lhitgame.game.gameentities.enums.DecorationsEnum;
import faust.lhitgame.game.gameentities.enums.EnemyEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.gameentities.enums.POIEnum;
import faust.lhitgame.game.gameentities.interfaces.Killable;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.instances.GameInstance;
import faust.lhitgame.game.instances.Spawner;
import faust.lhitgame.game.instances.impl.*;
import faust.lhitgame.game.music.MusicManager;
import faust.lhitgame.game.music.enums.TuneEnum;
import faust.lhitgame.game.rooms.areas.EmergedArea;
import faust.lhitgame.game.rooms.areas.WallArea;
import faust.lhitgame.game.rooms.enums.MapLayersEnum;
import faust.lhitgame.game.rooms.enums.MapObjNameEnum;
import faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import faust.lhitgame.game.rooms.enums.RoomTypeEnum;
import faust.lhitgame.game.splash.SplashManager;
import faust.lhitgame.game.textbox.manager.TextBoxManager;
import faust.lhitgame.utils.DepthComparatorUtils;
import faust.lhitgame.game.world.manager.WorldManager;
import faust.lhitgame.saves.RoomSaveEntry;

import java.util.*;


/**
 * Abstract room common logic
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AbstractRoom implements Spawner {

    /**
     * Boundaries for room changing
     */
    public static final float LEFT_BOUNDARY = 12;
    public static final float BOTTOM_BOUNDARY = 4;
    public static final float RIGHT_BOUNDARY = LHITGame.GAME_WIDTH - 12;
    public static final float TOP_BOUNDARY = LHITGame.GAME_HEIGHT - 24;

    // Permitted spawnable instances
    protected static final Map<String, String> permittedSpawnableInstance = new HashMap<String, String>() {{
        this.put(MeatInstance.class.getSimpleName(), EnemyEnum.MEAT.name());
        this.put(PortalInstance.class.getSimpleName(), EnemyEnum.PORTAL.name());
        this.put(POIInstance.class.getSimpleName(), POIEnum.ECHO_CORPSE.name());
        this.put(WillowispInstance.class.getSimpleName(), EnemyEnum.WILLOWISP.name());
    }};

    protected TiledMap tiledMap;
    protected OrthogonalTiledMapRenderer tiledMapRenderer;
    protected final MapObjects mapObjects;

    protected final List<POIInstance> poiList;
    protected final List<DecorationInstance> decorationList;
    protected final List<AnimatedInstance> enemyList;
    protected final List<WallArea> wallList;
    protected final List<EmergedArea> emergedAreaList;
    protected final PlayerInstance player;
    protected final RoomTypeEnum roomType;
    protected final SplashManager splashManager;
    protected final TextBoxManager textManager;
    protected final MusicManager musicManager;
    protected final AssetManager assetManager;
    protected final WorldManager worldManager;
    protected String roomFileName;

    protected boolean mustClearPOI = false;

    protected Map<RoomFlagEnum, Boolean> roomFlags;

    private GameInstance addedInstance; //Buffer for new enemies spawned during gameplay

    /**
     * Constructor
     *
     * @param roomType
     * @param worldManager
     * @param textManager
     * @param splashManager
     * @param player
     * @param camera
     * @param roomSaveEntry
     * @param roomFlags
     * @param musicManager
     */
    public AbstractRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, Map<RoomFlagEnum, Boolean> roomFlags, MusicManager musicManager) {
        Objects.requireNonNull(worldManager);
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(player);
        Objects.requireNonNull(roomType);
        Objects.requireNonNull(splashManager);
        Objects.requireNonNull(assetManager);

        // Clear world bodies, if present
        worldManager.clearBodies();

        this.assetManager = assetManager;
        this.worldManager = worldManager;

        this.roomFlags = roomFlags;

        // Load tiled map by name
        this.roomType = roomType;
        this.roomFileName = "terrains/" + roomType.getMapFileName();
        loadTiledMap(roomSaveEntry);

        // Extract mapObjects
        mapObjects = tiledMap.getLayers().get(MapLayersEnum.OBJECT_LAYER.getLayerName()).getObjects();

        // Set camera for rendering
        tiledMapRenderer.setView(camera);

        // Add content to room
        this.player = player;
        this.splashManager = splashManager;
        this.textManager = textManager;
        this.musicManager = musicManager;

        this.poiList = new ArrayList<>();
        this.decorationList = new ArrayList<>();
        this.enemyList = new ArrayList<>();
        this.wallList = new ArrayList<>();
        this.emergedAreaList = new ArrayList<>();

        // Place objects in room
        this.mapObjects.forEach(obj -> {

            // Prepare POI
            if (MapObjNameEnum.POI.name().equals(obj.getName())) {
                addObjAsPOI(obj, textManager, assetManager);
            }

            // Prepare decoration
            if (MapObjNameEnum.DECO.name().equals(obj.getName())) {
                addObjAsDecoration(obj, assetManager);
            }

            // Prepare enemy if they are enabled
            if (!roomFlags.get(RoomFlagEnum.DISABLED_ENEMIES) && MapObjNameEnum.ENEMY.name().equals(obj.getName())) {
                addObjAsEnemy(obj, assetManager, false);
            }

            // Prepare enemy (casual choice)
            if (MapObjNameEnum.WALL.name().equals(obj.getName())) {
                addObjAsWall(obj);
            }

            // Prepare enemy (casual choice)
            if (MapObjNameEnum.EMERGED.name().equals(obj.getName())) {
                addObjAsEmerged(obj);
            }
        });

        worldManager.insertPlayerIntoWorld(player, player.getStartX(), player.getStartY());
        worldManager.insertPOIIntoWorld(poiList);
        worldManager.insertDecorationsIntoWorld(decorationList);
        worldManager.insertEnemiesIntoWorld(enemyList);
        worldManager.insertWallsIntoWorld(wallList);
        worldManager.insertEmergedAreasIntoWorld(emergedAreaList);
        player.changePOIList(poiList);

        // Do other stuff
        this.onRoomEnter(roomType, worldManager, textManager, splashManager, player, camera, assetManager);
    }

    /**
     * Add invisible walls
     *
     * @param obj
     */
    protected void addObjAsWall(MapObject obj) {

        RectangleMapObject mapObject = (RectangleMapObject) obj;

        wallList.add(new WallArea(mapObject.getRectangle()));
    }

    /**
     * Add invisible emerged areas
     *
     * @param obj
     */
    protected void addObjAsEmerged(MapObject obj) {

        PolygonMapObject mapObject = (PolygonMapObject) obj;

        emergedAreaList.add(new EmergedArea(mapObject.getPolygon()));
    }

    /**
     * Implements tiled map load
     *
     * @param roomSaveEntry if needed
     */
    protected abstract void loadTiledMap(RoomSaveEntry roomSaveEntry);

    /**
     * Add a object as POI
     *
     * @param obj
     * @param textManager
     */
    protected void addObjAsPOI(MapObject obj, TextBoxManager textManager, AssetManager assetManager) {

        POIEnum poiType = POIEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(poiType);

        Gdx.app.log("DEBUG", "guaranteedMorgengabe: " + roomFlags.get(RoomFlagEnum.GUARANTEED_MORGENGABE));
        Gdx.app.log("DEBUG", "guaranteedHerbs: " + roomFlags.get(RoomFlagEnum.GUARANTEED_HERBS));

        poiList.add(new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, splashManager, assetManager,
                roomFlags.get(RoomFlagEnum.GUARANTEED_MORGENGABE)));
    }

    /**
     * Add a object as Decoration
     *
     * @param obj MapObject to add
     */
    protected void addObjAsDecoration(MapObject obj, AssetManager assetManager) {

        DecorationsEnum decoType = DecorationsEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(decoType);

        decorationList.add(new DecorationInstance(
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                decoType, assetManager));
    }

    /**
     * Add a object as Enemy
     *
     * @param obj            MapObject to add
     * @param addNewInstance
     */
    protected void addObjAsEnemy(MapObject obj, AssetManager assetManager, boolean addNewInstance) {

        // Enemies are usually dynamically determined, with a couple of exceptional cases
        // which should be set as "type" property on MapObject
        EnemyEnum enemyEnum = EnemyEnum.UNDEFINED;
        if (obj.getProperties().containsKey("type")) {
            enemyEnum = EnemyEnum.getFromString((String) obj.getProperties().get("type"));
            Objects.requireNonNull(enemyEnum);
        }

        switch (enemyEnum) {
            case PORTAL: {
                addedInstance = new PortalInstance(assetManager);
                break;
            }
            case WILLOWISP: {
                addedInstance = new WillowispInstance(
                        (float) obj.getProperties().get("x"),
                        (float) obj.getProperties().get("y"),
                        player,
                        assetManager);
                break;
            }
            case HIVE: {
                addedInstance = new HiveInstance(
                        (float) obj.getProperties().get("x"),
                        (float) obj.getProperties().get("y"),
                        assetManager,
                        textManager);

                //Show splash only the first time
                if (!roomFlags.get(RoomFlagEnum.FIRST_HIVE_ENCOUNTERED))
                    splashManager.setSplashToShow("splash.hive");

                roomFlags.put(RoomFlagEnum.FIRST_HIVE_ENCOUNTERED, true);
                break;
            }
            case MEAT: {
                addedInstance = new MeatInstance(
                        (float) obj.getProperties().get("x"),
                        (float) obj.getProperties().get("y"),
                        player, assetManager);

                break;
            }
            case SPITTER: {
                addedInstance = new SpitterInstance(
                        (float) obj.getProperties().get("x"),
                        (float) obj.getProperties().get("y"),
                        assetManager, textManager, this);

                splashManager.setSplashToShow("splash.spitter");
                break;
            }
            default: {

                if (roomFlags.get(RoomFlagEnum.GUARDANTEED_BOUNDED)) {
                    addedInstance = new BoundedInstance(
                            (float) obj.getProperties().get("x"),
                            (float) obj.getProperties().get("y"),
                            player,
                            assetManager);

                    //Show splash only the first time
                    if (!roomFlags.get(RoomFlagEnum.FIRST_BOUNDED_ENCOUNTERED))
                        splashManager.setSplashToShow("splash.bounded");

                    roomFlags.put(RoomFlagEnum.FIRST_BOUNDED_ENCOUNTERED, true);
                } else {
                    addedInstance = new StrixInstance(
                            (float) obj.getProperties().get("x"),
                            (float) obj.getProperties().get("y"),
                            player,
                            assetManager);

                    //Show splash only the first time
                    if (!roomFlags.get(RoomFlagEnum.FIRST_STRIX_ENCOUNTERED))
                        splashManager.setSplashToShow("splash.strix");

                    roomFlags.put(RoomFlagEnum.FIRST_STRIX_ENCOUNTERED, true);
                }

            }
        }

        // If is not a spawned instance (usually MeatInstance), add it right now
        if (!addNewInstance) {
            enemyList.add((AnimatedInstance) addedInstance);
            addedInstance = null;
        }
    }

    /**
     * Method for additional room initialization
     *
     * @param roomType
     * @param worldManager
     * @param textManager
     * @param splashManager
     * @param camera
     */
    protected abstract void onRoomEnter(RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, OrthographicCamera camera, AssetManager assetManager);

    /**
     * Draws room background terrain
     */
    public void drawRoomTerrain() {
        MapLayers mapLayers = tiledMap.getLayers();
        TiledMapTileLayer terrainLayer = (TiledMapTileLayer) mapLayers.get(MapLayersEnum.TERRAIN_LAYER.getLayerName());

        //Overlay layer should is required
        Objects.requireNonNull(terrainLayer);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(terrainLayer);
        tiledMapRenderer.getBatch().end();
    }

    /**
     * Draws room overlay
     */
    public void drawRoomOverlay() {
        MapLayers mapLayers = tiledMap.getLayers();
        TiledMapTileLayer overlayLayer = (TiledMapTileLayer) mapLayers.get(MapLayersEnum.OVERLAY_LAYER.getLayerName());

        //Overlay layer should not be required
        if (Objects.isNull(overlayLayer)) {
            return;
        }

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(overlayLayer);
        tiledMapRenderer.getBatch().end();
    }

    /**
     * Draws room contents
     *
     * @param batch
     * @param stateTime
     */
    public void drawRoomContents(final SpriteBatch batch, float stateTime) {

        List<GameInstance> allInstance = new ArrayList<>();

        allInstance.addAll(poiList);
        allInstance.addAll(decorationList);
        allInstance.add(player);
        allInstance.addAll(enemyList);

        // Sort by Y for depth effect. If decoration is interacted, priority is lowered
        allInstance.sort(DepthComparatorUtils::compareEntities);

        allInstance.forEach((i) -> i.draw(batch, stateTime));

    }

    /**
     * Disposes the terrain and the contents of the room
     */
    public void dispose() {
        textManager.removeAllBoxes();
        tiledMap.dispose();
        enemyList.forEach(AnimatedInstance::dispose);
        decorationList.forEach(DecorationInstance::dispose);
        poiList.forEach(POIInstance::dispose);
        wallList.forEach(WallArea::dispose);
        emergedAreaList.forEach(EmergedArea::dispose);
    }

    public RoomTypeEnum getRoomType() {
        return roomType;
    }

    public synchronized void doRoomContentsLogic(float stateTime) {

        // Do Player logic
        player.doLogic(stateTime, this);

        //Stop music
        if(player.isDead() && musicManager.isPlaying()){
            musicManager.stopMusic();
        }

        // Do enemy logic
        enemyList.forEach((ene) -> {

            ene.doLogic(stateTime, this);

            if (ene instanceof SpitterInstance && ((Killable) ene).isDead()) {
                musicManager.stopMusic();
                player.setPrepareEndgame(true);
            } else if (enemyList.size() == 1 && ((Killable) ene).isDead()) {
                //Changing music based on enemy behaviour and number
                musicManager.playMusic(TuneEnum.DANGER, true);
            } else if (!player.isDead() && (!RoomTypeEnum.FINAL.equals(roomType) && !RoomTypeEnum.CHURCH_ENTRANCE.equals(roomType)) &&
                    !GameBehavior.IDLE.equals(ene.getCurrentBehavior())) {
                musicManager.playMusic(TuneEnum.ATTACK, 0.65f, true);
            }
        });

        // If there is an instance to add, do it and clean reference
        if (!Objects.isNull(addedInstance)) {
            enemyList.add((AnimatedInstance) addedInstance);
            addedInstance = null;
        }

        // Dispose enemies
        enemyList.forEach(ene -> {
            if (ene.isDisposable()) {
                ene.dispose();
            }
        });

        // Remove some dead enemies
        enemyList.removeIf(ene -> ene instanceof MeatInstance && ((Killable) ene).isDead());
    }

    /**
     * @return true if all Poi are been examined
     */
    public boolean arePoiCleared() {
        //FIXME handle multiple POI
        return this.poiList.stream().allMatch(POIInstance::isAlreadyExamined);
    }

    /**
     *
     * @return
     */
    public List <AnimatedInstance> getEnemyList(){
        return enemyList;
    }

    @Override
    public synchronized <T extends GameInstance> void spawnInstance(Class<T> instanceClass, float startX, float startY) {

        if (!Objects.isNull(addedInstance)) {
            return;
        }

        //Create a stub MapObject
        final MapObject mapObjectStub = new MapObject();
        mapObjectStub.getProperties().put("x", startX);
        mapObjectStub.getProperties().put("y", startY);
        mapObjectStub.getProperties().put("type", permittedSpawnableInstance.get(instanceClass.getSimpleName()));

        //Insert last enemy into world
        if (instanceClass.equals(MeatInstance.class) || instanceClass.equals(WillowispInstance.class)) {
            addObjAsEnemy(mapObjectStub, assetManager, true);
            worldManager.insertEnemiesIntoWorld(Collections.singletonList((AnimatedInstance) addedInstance));
        } else if (instanceClass.equals(POIInstance.class)) {
            addObjAsPOI(mapObjectStub, textManager, assetManager);
            POIInstance lastPOIInstance = poiList.get(poiList.size() - 1);
            worldManager.insertPOIIntoWorld(Collections.singletonList(lastPOIInstance));
            roomFlags.put(RoomFlagEnum.ALREADY_EXAMINED_POIS, false);
            player.changePOIList(poiList);
        } else if (instanceClass.equals(PortalInstance.class)) {
            addObjAsEnemy(mapObjectStub, assetManager, true);
        }
    }

    /**
     *
     * @return musicManager
     */
    public MusicManager getMusicManager() {
        return musicManager;
    }

    public abstract void onRoomLeave();

    public Map<RoomFlagEnum, Boolean> getRoomFlags() {
        return roomFlags;
    }
}
