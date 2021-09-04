package faust.lhitgame.game.world.manager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.instances.GameInstance;
import faust.lhitgame.game.instances.impl.DecorationInstance;
import faust.lhitgame.game.instances.impl.EchoActorInstance;
import faust.lhitgame.game.instances.impl.POIInstance;
import faust.lhitgame.game.instances.impl.PlayerInstance;
import faust.lhitgame.game.rooms.areas.EmergedArea;
import faust.lhitgame.game.rooms.areas.WallArea;

import java.util.List;
import java.util.Objects;


/**
 * Wraps Box2D world
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class WorldManager {
    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;

    private final World world;

    public WorldManager() {
        this.world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionManager());

    }

    /**
     * Makes the world step to next
     */
    public void doStep() {
        world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }

    public World getWorld() {
        return world;
    }

    public void dispose() {
        world.dispose();
    }

    /**
     * Inserts a PlayerInstance into Box2D World
     *
     * @param playerInstance
     * @param x
     * @param y
     */
    public void insertPlayerIntoWorld(final PlayerInstance playerInstance, float x, float y) {
        Objects.requireNonNull(playerInstance);

        Float horizontalVelocity = null;
        Float verticalVelocity = null;

        // If player has already a linear velocity, restore after body
        if (!Objects.isNull(playerInstance.getBody())) {
            horizontalVelocity = playerInstance.getBody().getLinearVelocity().x;
            verticalVelocity = playerInstance.getBody().getLinearVelocity().y;
        }

        // Insert into world generating new body
        this.insertIntoWorld(playerInstance, x, y);

        playerInstance.setStartX(0);
        playerInstance.setStartY(0);

        if (!Objects.isNull(verticalVelocity) || !Objects.isNull(horizontalVelocity)) {
            playerInstance.getBody().setLinearVelocity(horizontalVelocity, verticalVelocity);
        }
    }

    /**
     * Inserts a GameInstance into Box2D World
     *
     * @param instance the instance to insert
     * @param x
     * @param y
     */
    private void insertIntoWorld(final GameInstance instance, float x, float y) {
        Objects.requireNonNull(instance);
        instance.createBody(this.world, x, y);
    }

    /**
     * Insert a list of POI into world, in random positions
     *
     * @param poiList
     */
    public void insertPOIIntoWorld(final List<POIInstance> poiList) {
        Objects.requireNonNull(poiList);

        poiList.forEach((poi) -> this.insertIntoWorld(poi, poi.getStartX(), poi.getStartY()));
    }

    /**
     * Insert a list of Decorations into world, in random positions
     *
     * @param decorationInstances
     */
    public void insertDecorationsIntoWorld(List<DecorationInstance> decorationInstances) {
        Objects.requireNonNull(decorationInstances);

        decorationInstances.forEach((deco) -> this.insertIntoWorld(deco, deco.getStartX(), deco.getStartY()));
    }

    /**
     * Destroy all bodies currently in Box2D world
     */
    public void clearBodies() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        bodies.forEach(this.world::destroyBody);
    }

    /**
     * Insert a list of Enemies into world, in random positions
     *
     * @param enemiesInstance
     */
    public void insertEnemiesIntoWorld(List<AnimatedInstance> enemiesInstance) {
        Objects.requireNonNull(enemiesInstance);

        enemiesInstance.forEach((e) -> this.insertIntoWorld(e, e.getStartX(), e.getStartY()));
    }

    /**
     * Insert static walls into world
     *
     * @param wallList
     */
    public void insertWallsIntoWorld(List<WallArea> wallList) {
        Objects.requireNonNull(wallList);

        wallList.forEach((w) -> w.createBody(this.world));
    }

    /**
     * Insert static emerged areas into world
     *
     * @param areasList
     */
    public void insertEmergedAreasIntoWorld(List<EmergedArea> areasList) {
        Objects.requireNonNull(areasList);

        areasList.forEach((a) -> a.createBody(this.world));
    }

    /**
     * @param echoActors
     */
    public void insertEchoActorsIntoWorld(List<EchoActorInstance> echoActors) {
        Objects.requireNonNull(echoActors);

        echoActors.forEach((a) -> a.createBody(this.world, a.getStartX(), a.getStartY()));
    }
}
