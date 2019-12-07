package faust.lhipgame.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.POIInstance;
import faust.lhipgame.instances.PlayerInstance;

import java.util.List;
import java.util.Objects;


/**
 * Wraps Box2D world
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class WorldManager {
    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;

    private World world;

    public WorldManager() {
        this.world = new World(new Vector2(0, -10), true);
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
        this.insertIntoWorld(playerInstance, x, y, false);
    }

    /**
     * Inserts a GameInstance into Box2D World
     *
     * @param instance     the instance to insert
     * @param x
     * @param y
     * @param isStaticBody true if is a StaticBody
     *
     */
    private void insertIntoWorld(final GameInstance instance, float x, float y, final boolean isStaticBody) {
        instance.createBody(this.world, x, y, isStaticBody);
    }

    /**
     * Insert a list of POI into world, in random positions
     * @param poiList
     */
    public void insertPOIIntoRoom(List<POIInstance> poiList) {
        for (POIInstance poi : poiList) {
            this.insertIntoWorld(poi, (float)Math.random() * LHIPGame.GAME_WIDTH,  (float)Math.random() * LHIPGame.GAME_HEIGHT, true);
        }
    }
}
