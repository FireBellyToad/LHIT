package faust.lhipgame.game.instances;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import faust.lhipgame.game.gameentities.GameEntity;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;

import java.util.Objects;

public abstract class AnimatedInstance extends GameInstance {

    protected static final int LINE_OF_SIGHT = 60;
    protected int damage = 0;

    protected GameBehavior currentBehavior = GameBehavior.IDLE;
    protected Direction currentDirection = Direction.UNUSED;

    protected Body hitBox;

    public AnimatedInstance(final GameEntity entity) {
        super(entity);
    }

    /**
     * Handles the LivingEntity game logic
     * @param stateTime
     */
    public abstract void doLogic(float stateTime);

    /**
     * Utility for extracting Direction from a directionNormal normal
     */
    protected Direction extractDirectionFromNormal(Vector2 directionNormal) {

        if (directionNormal.x <= -0.5) {
            return Direction.LEFT;
        } else if (directionNormal.x > 0.5) {
            return Direction.RIGHT;
        }

        if (directionNormal.y < 0) {
            return Direction.DOWN;
        } else {
            return Direction.UP;
        }
    }

    @Override
    public void dispose() {
        if(!Objects.isNull(hitBox)){
            this.hitBox.getFixtureList().forEach(f ->
                    hitBox.destroyFixture(f));
        }
        super.dispose();
    }

    public GameBehavior getCurrentBehavior() {
        return currentBehavior;
    }

    /**
     * Alter state time for different animation speed based on current behaviour
     *
     * @param stateTime
     * @return
     */
    protected abstract float mapStateTimeFromBehaviour(float stateTime);

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
