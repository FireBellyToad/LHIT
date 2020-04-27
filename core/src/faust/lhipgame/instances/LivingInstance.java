package faust.lhipgame.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

public abstract class LivingInstance extends GameInstance {

    protected static final int LINE_OF_SIGHT = 60;
    protected int damage = 0;

    protected GameBehavior currentBehavior = GameBehavior.IDLE;
    protected Direction currentDirection = Direction.UNUSED;

    public LivingInstance(final GameEntity entity) {
        super(entity);
    }

    /**
     * Handles the LivingEntity game logic
     */
    public abstract void doLogic();


    /**
     * Method for hurting the LivingEntity
     *
     * @param damageReceived to be subtracted
     */
    public void hurt(int damageReceived) {
        this.damage += Math.min(((LivingEntity) entity).getResistance(), damageReceived);
        Gdx.app.log("INFO","Instance " + this.getClass().getSimpleName() + " total damage "+ damage );
    }


    /**
     * @return true if the damage is greater or equal than the resitance
     */
    public boolean isDead() {
        return this.damage >= ((LivingEntity) entity).getResistance();
    }

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
}
