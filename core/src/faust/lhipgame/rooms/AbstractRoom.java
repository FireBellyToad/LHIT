package faust.lhipgame.rooms;

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
import faust.lhipgame.gameentities.Killable;
import faust.lhipgame.gameentities.enums.DecorationsEnum;
import faust.lhipgame.gameentities.enums.POIEnum;
import faust.lhipgame.instances.AnimatedInstance;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.rooms.areas.EmergedArea;
import faust.lhipgame.rooms.areas.WallArea;
import faust.lhipgame.instances.impl.DecorationInstance;
import faust.lhipgame.instances.impl.POIInstance;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.instances.impl.StrixInstance;
import faust.lhipgame.rooms.enums.MapLayersEnum;
import faust.lhipgame.rooms.enums.MapObjNameEnum;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

import java.util.ArrayList;
import java.util.List;
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
    protected RoomType roomType;
    protected String roomFileName;
    protected SplashManager splashManager;
    protected TextManager textManager;

    protected boolean mustClearPOI = false;

    /**
     * Constructor without additional loader argouments
     *
     * @param roomType
     * @param worldManager
     * @param textManager
     * @param splashManager
     * @param player
     * @param camera
     */
    @SuppressWarnings("unchecked")
    public AbstractRoom(final RoomType roomType, final WorldManager worldManager, final TextManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera) {
        this(roomType, worldManager, textManager, splashManager, player, camera, null);
    }

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
     */
    public AbstractRoom(final RoomType roomType, final WorldManager worldManager, final TextManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final RoomSaveEntry roomSaveEntry) {
        Objects.requireNonNull(worldManager);
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(player);
        Objects.requireNonNull(roomType);

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
        mapObjects.forEach(obj ->{

            // Prepare POI
            if (MapObjNameEnum.POI.name().equals(obj.getName())) {
                addObjAsPOI(obj, textManager);
            }

            // Prepare decoration
            if (MapObjNameEnum.DECO.name().equals(obj.getName())) {
                addObjAsDecoration(obj);
            }

            // Prepare enemy (casual choice)
            if (MapObjNameEnum.ENEMY.name().equals(obj.getName()) && MathUtils.randomBoolean()) {
                addObjAsEnemy(obj);
                splashManager.setSplashToShow("splash.strix");
            }

            // Prepare enemy (casual choice)
            if (MapObjNameEnum.WALL.name().equals(obj.getName())) {
                addObjAsWall(obj);
                ///splashManager.setSplashToShow("splash.strix");
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
        this.initRoom(roomType, worldManager, textManager, splashManager, player, camera);
    }

    /**
     * Add invisible walls
     * @param obj
     */
    protected void addObjAsWall(MapObject obj){

        RectangleMapObject mapObject = (RectangleMapObject) obj;

        wallList.add(new WallArea(mapObject.getRectangle()));
    }

    /**
     * Add invisible emerged areas
     * @param obj
     */
    protected void addObjAsEmerged(MapObject obj){

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
    protected void addObjAsPOI(MapObject obj, TextManager textManager) {

        POIEnum poiType = POIEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(poiType);

        poiList.add(new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, player, splashManager));
    }

    ;

    /**
     * Add a object as Decoration
     *
     * @param obj MapObject to add
     */
    protected void addObjAsDecoration(MapObject obj) {

        DecorationsEnum decoType = DecorationsEnum.getFromString((String) obj.getProperties().get("type"));
        Objects.requireNonNull(decoType);

        decorationList.add(new DecorationInstance(
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                decoType));
    }

    /**
     * Add a object as Enemy
     *
     * @param obj MapObject to add
     */
    protected void addObjAsEnemy(MapObject obj) {

        //FIXME add different enemy types
        enemyList.add(new StrixInstance(
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                player));
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
    protected abstract void initRoom(RoomType roomType, final WorldManager worldManager, final TextManager textManager, final SplashManager splashManager, final PlayerInstance player, OrthographicCamera camera);

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
        if (o1.getBody().getPosition().y < o2.getBody().getPosition().y ||
                (o1 instanceof StrixInstance && ((StrixInstance) o1).isAttachedToPlayer()) ||
                (o2 instanceof DecorationInstance && ((DecorationInstance) o2).getInteracted())) {
            return 1;
        } else if (o1.getBody().getPosition().y > o2.getBody().getPosition().y ||
                (o2 instanceof StrixInstance && ((StrixInstance) o2).isAttachedToPlayer())) {
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

    public RoomType getRoomType() {
        return roomType;
    }

    public void doRoomContentsLogic(float stateTime) {
        // Do Player logic
        if (!player.isDead())
            player.doLogic(stateTime);
        else {
            splashManager.setSplashToShow("splash.gameover");
        }

        // Do enemy logic
        enemyList.forEach((ene) -> {

            ene.doLogic(stateTime);

            if (((Killable) ene).isDead())
                ene.dispose();

        });

        // Remove dead enemies
        enemyList.removeIf(ene -> ((Killable) ene).isDead());

    }

    /**
     *
     * @return true if all Poi are been examined
     */
    public boolean arePoiCleared(){
        //FIXME handle multiple POI
        return this.poiList.stream().allMatch(poiInstance -> poiInstance.isAlreadyExamined());
    }

    ;
}
