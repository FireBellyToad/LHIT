package faust.lhipgame.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.DecorationInstance;
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
    public void insertPOIIntoWorld(List<POIInstance> poiList) {

        poiList.forEach((poi)-> {
//
//            float randomX = MathUtils.random(LHIPGame.GAME_WIDTH);
//            float randomY = MathUtils.random(LHIPGame.GAME_HEIGHT);
//
//            // Clamping the values for keeping it inside the screen
//            randomX = MathUtils.clamp(randomX,0, LHIPGame.GAME_WIDTH-32);
//            randomY = MathUtils.clamp(randomY,0, LHIPGame.GAME_HEIGHT-32);

            this.insertIntoWorld(poi, poi.getStartX(), poi.getStartY(), true);
        });
    }


    /**
     * Insert a list of Decorations into world, in random positions
     * @param decorationInstances
     */
    public void insertDecorationsIntoWorld(List<DecorationInstance> decorationInstances) {

        decorationInstances.forEach((deco)-> {
//
//            float randomX = MathUtils.random(LHIPGame.GAME_WIDTH);
//            float randomY = MathUtils.random(LHIPGame.GAME_HEIGHT);
//
//            // Clamping the values for keeping it inside the screen
//            randomX = MathUtils.clamp(randomX,0, LHIPGame.GAME_WIDTH-32);
//            randomY = MathUtils.clamp(randomY,0, LHIPGame.GAME_HEIGHT-32);

            this.insertIntoWorld(deco, deco.getStartX(), deco.getStartY(), true);
        });
    }

}
