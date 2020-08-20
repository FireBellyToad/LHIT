package faust.lhipgame.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.gameentities.enums.DecorationsEnum;
import faust.lhipgame.gameentities.enums.POIEnum;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.LivingInstance;
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
    protected List<LivingInstance> enemyList;
    protected PlayerInstance player;
    protected RoomType roomType;
    protected String roomFileName;
    protected SplashManager splashManager;

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
     * @param additionalLoadArgs
     */
    public AbstractRoom(final RoomType roomType, final WorldManager worldManager, final TextManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, Object... additionalLoadArgs) {
        Objects.requireNonNull(worldManager);
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(player);
        Objects.requireNonNull(roomType);

        // Load tiled map by name
        this.roomType = roomType;
        this.roomFileName = "terrains/" + roomType.getMapFileName();
        loadTiledMap(additionalLoadArgs);

        // Extract mapObjects
        mapObjects = tiledMap.getLayers().get(MapLayersEnum.OBJECT_LAYER.ordinal()).getObjects();

        // Set camera for rendering
        tiledMapRenderer.setView(camera);

        // Add content to room
        this.player = player;
        this.splashManager = splashManager;
        poiList = new ArrayList<>();
        decorationList = new ArrayList<>();
        enemyList = new ArrayList<>();


        // Place objects in room
        for (MapObject obj : mapObjects) {

            // Prepare POI
            if (MapObjNameEnum.POI.name().equals(obj.getName())) {
                addObjAsPOI(obj, textManager);
            }

            // Prepare decoration
            if (MapObjNameEnum.DECO.name().equals(obj.getName())) {
                addObjAsDecoration(obj);
            }

        }

        worldManager.clearBodies();
        worldManager.insertPlayerIntoWorld(player, player.getStartX(), player.getStartY());
        worldManager.insertPOIIntoWorld(poiList);
        worldManager.insertDecorationsIntoWorld(decorationList);
        worldManager.insertEnemiesIntoWorld(enemyList);
        player.changePOIList(poiList);

        // Do other stuff
        this.initRoom(roomType, worldManager, textManager, splashManager, player, camera);
    }

    /**
     * Implements tiled map load
     *
     * @param additionalLoadArguments if needed
     */
    protected abstract void loadTiledMap(Object[] additionalLoadArguments);

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

        //FIXME
        enemyList.add(new StrixInstance(
                LHIPGame.GAME_WIDTH / 2,
                LHIPGame.GAME_HEIGHT / 2,
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
        tiledMap.dispose();
        enemyList.forEach((ene) -> ene.dispose());
        decorationList.forEach((deco) -> deco.dispose());
        poiList.forEach((poi) -> poi.dispose());
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void doRoomContentsLogic(float stateTime) {
        // Do Player logic
        if (!player.isDead())
            player.doLogic(stateTime);
        else {
            Gdx.app.exit();
        }

        // Do enemy logic
        enemyList.forEach((ene) -> {

            if (!ene.isDead())
                ene.doLogic(stateTime);
            else
                ene.dispose();

        });

        enemyList.removeIf(ene -> ene.isDead());

    }

    ;
}
