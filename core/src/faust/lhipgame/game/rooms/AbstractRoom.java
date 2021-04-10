package faust.lhipgame.game.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.gameentities.Fightable;
import faust.lhipgame.game.gameentities.enums.DecorationsEnum;
import faust.lhipgame.game.gameentities.enums.POIEnum;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.impl.*;
import faust.lhipgame.game.rooms.areas.EmergedArea;
import faust.lhipgame.game.rooms.areas.WallArea;
import faust.lhipgame.game.rooms.enums.MapLayersEnum;
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
 * Abstract room common logic
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AbstractRoom {

    /**
     * Boundaries for room changing
     */
    public static final float LEFT_BOUNDARY = 12;
    public static final float BOTTOM_BOUNDARY = 4;
    public static final float RIGHT_BOUNDARY = LHIPGame.GAME_WIDTH - 12;
    public static final float TOP_BOUNDARY = LHIPGame.GAME_HEIGHT - 24;

    protected TiledMap tiledMap;
    protected TiledMapRenderer tiledMapRenderer;
    protected MapObjects mapObjects;

    protected List<POIInstance> poiList;
    protected List<DecorationInstance> decorationList;
    protected List<AnimatedInstance> enemyList;
    protected List<WallArea> wallList;
    protected List<EmergedArea> emergedAreaList;
    protected PlayerInstance player;
    protected RoomTypeEnum roomType;
    protected String roomFileName;
    protected SplashManager splashManager;
    protected TextBoxManager textManager;

    protected boolean mustClearPOI = false;

    protected final Map<RoomFlagEnum, Boolean> roomFlags;

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
     */
    public AbstractRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, Map<RoomFlagEnum, Boolean> roomFlags) {
        Objects.requireNonNull(worldManager);
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(player);
        Objects.requireNonNull(roomType);

        this.roomFlags = roomFlags;

        // Load tiled map by name
        this.roomType = roomType;
        this.roomFileName = "terrains/" + roomType.getMapFileName();
        loadTiledMap(roomSaveEntry);

        // Extract mapObjects
        mapObjects = tiledMap.getLayers().get(MapLayersEnum.OBJECT_LAYER.ordinal()).getObjects();

        // Set camera for rendering
        tiledMapRenderer.setView(camera);

        // Add content to room
        this.player = player;
        this.splashManager = splashManager;
        this.textManager = textManager;
        poiList = new ArrayList<>();
        decorationList = new ArrayList<>();
        enemyList = new ArrayList<>();
        wallList = new ArrayList<>();
        emergedAreaList = new ArrayList<>();

        // Place objects in room
        mapObjects.forEach(obj -> {

            // Prepare POI
            if (MapObjNameEnum.POI.name().equals(obj.getName())) {
                addObjAsPOI(obj, textManager, assetManager);
            }

            // Prepare decoration
            if (MapObjNameEnum.DECO.name().equals(obj.getName())) {
                addObjAsDecoration(obj, assetManager);
            }

            // Prepare enemy (casual choice)
            if (MapObjNameEnum.ENEMY.name().equals(obj.getName())) {
                addObjAsEnemy(obj, assetManager);
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

        worldManager.clearBodies();
        worldManager.insertPlayerIntoWorld(player, player.getStartX(), player.getStartY());
        worldManager.insertPOIIntoWorld(poiList);
        worldManager.insertDecorationsIntoWorld(decorationList);
        worldManager.insertEnemiesIntoWorld(enemyList);
        worldManager.insertWallsIntoWorld(wallList);
        worldManager.insertEmergedAreasIntoWorld(emergedAreaList);
        player.changePOIList(poiList);

        // Do other stuff
        this.initRoom(roomType, worldManager, textManager, splashManager, player, camera, assetManager);
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

        poiList.add(new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, player, splashManager, assetManager,
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
     * @param obj MapObject to add
     */
    protected void addObjAsEnemy(MapObject obj, AssetManager assetManager) {

        AnimatedInstance enemyInstance = null;
        String enemyType = (String) obj.getProperties().get("type");

        //Improve
        if ("HIVE".equals(enemyType)) {
            enemyInstance = new HiveInstance(
                    (float) obj.getProperties().get("x"),
                    (float) obj.getProperties().get("y"),
                    assetManager,
                    textManager);
        } else if (roomFlags.get(RoomFlagEnum.GUARDANTEED_BOUNDED)) {
            enemyInstance = new BoundedInstance(
                    (float) obj.getProperties().get("x"),
                    (float) obj.getProperties().get("y"),
                    player,
                    assetManager);

            if (MathUtils.randomBoolean())
                splashManager.setSplashToShow("splash.bounded");
        } else {
            enemyInstance = new StrixInstance(
                    (float) obj.getProperties().get("x"),
                    (float) obj.getProperties().get("y"),
                    player,
                    assetManager);

            if (MathUtils.randomBoolean())
                splashManager.setSplashToShow("splash.strix");
        }


        enemyList.add(enemyInstance);
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
    protected abstract void initRoom(RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, OrthographicCamera camera, AssetManager assetManager);

    /**
     * Draws room background
     */
    public void drawRoomBackground() {
        tiledMapRenderer.render();
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
        allInstance.sort((o1, o2) -> compareEntities(o1, o2));

        allInstance.forEach((i) -> {
            i.draw(batch, stateTime);
        });

    }

    // Compares two GameInstances by y depth
    protected int compareEntities(GameInstance o1, GameInstance o2) {

        //Special conditions to place object always on higher depth, usually
        //for avoiding that objects laying on the ground cover taller ones
        if ((o2 instanceof Fightable && ((Fightable) o2).isDead()) ||
                (o1 instanceof StrixInstance && ((StrixInstance) o1).isAttachedToPlayer()) ||
                (o2 instanceof DecorationInstance && ((DecorationInstance) o2).getInteracted()) ||
                (o2 instanceof POIInstance && POIEnum.SKELETON.equals(((POIInstance) o2).getType()))) {
            return 1;
        }

        if ((o1 instanceof Fightable && ((Fightable) o1).isDead()) ||
                (o2 instanceof StrixInstance && ((StrixInstance) o2).isAttachedToPlayer())) {
            return -1;
        }

        //or else just sort by Y axis
        if((o1.getBody().getPosition().y < o2.getBody().getPosition().y)){
            return 1;
        } else if (o1.getBody().getPosition().y > o2.getBody().getPosition().y) {
            return -1;
        }


        return 0;
    }

    /**
     * Disposes the terrain and the contents of the room
     */
    public void dispose() {
        textManager.removeAllBoxes();
        tiledMap.dispose();
        enemyList.forEach((ene) -> ene.dispose());
        decorationList.forEach((deco) -> deco.dispose());
        poiList.forEach((poi) -> poi.dispose());
        wallList.forEach((wall) -> wall.dispose());
        emergedAreaList.forEach((emergedArea) -> emergedArea.dispose());
    }

    public RoomTypeEnum getRoomType() {
        return roomType;
    }

    public void doRoomContentsLogic(float stateTime) {
        // Do Player logic
        if (!player.isDead())
            player.doLogic(stateTime);

        // Do enemy logic
        enemyList.forEach((ene) -> {

            ene.doLogic(stateTime);

            if (((Fightable) ene).isDead())
                ene.dispose();

        });

    }

    /**
     * @return true if all Poi are been examined
     */
    public boolean arePoiCleared() {
        //FIXME handle multiple POI
        return this.poiList.stream().allMatch(poiInstance -> poiInstance.isAlreadyExamined());
    }

}
