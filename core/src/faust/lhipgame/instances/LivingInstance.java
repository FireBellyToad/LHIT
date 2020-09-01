package faust.lhipgame.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.Objects;

public abstract class LivingInstance extends GameInstance {

    protected static final int LINE_OF_SIGHT = 60;
    protected int damage = 0;

    protected GameBehavior currentBehavior = GameBehavior.IDLE;
    protected Direction currentDirection = Direction.UNUSED;

    protected Body hitBox;

    public LivingInstance(final GameEntity entity) {
        super(entity);
    }

    /**
     * Handles the LivingEntity game logic
     * @param stateTime
     */
    public abstract void doLogic(float stateTime);


    /**
     * Method for hurting the LivingEntity
     *
     * @param damageReceived to be subtracted
     */
    public void hurt(int damageReceived) {
        if(!GameBehavior.HURT.equals(currentBehavior)){
            this.damage += Math.min(((LivingEntity) entity).getResistance(), damageReceived);
            Gdx.app.log("DEBUG","Instance " + this.getClass().getSimpleName() + " total damage "+ damage );
            postHurtLogic();
        }

    }

    /**
     * Logic to be done after being hurt
     */
    protected abstract void postHurtLogic();


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

    @Override
    public void dispose() {
        if(!Objects.isNull(hitBox)){
            this.hitBox.getFixtureList().forEach(f ->
                    hitBox.destroyFixture(f));
        }
        super.dispose();
    }

    public int getDamage() {
        return damage;
    }

    public int getResistance(){
        return ((LivingEntity) entity).getResistance();
    }

    public int getDamageDelta() {
        return  ((LivingEntity) entity).getResistance() - damage;
    }

    public GameBehavior getCurrentBehavior() {
        return currentBehavior;
    }


}
