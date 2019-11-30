package faust.lhipgame.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.instances.GameInstance;

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

    public void insertIntoWorld(GameInstance instance,int x, int y) {
        instance.createBody(getWorld(),x,y);
    }
}
