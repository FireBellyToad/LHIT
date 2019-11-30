package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.HashMap;
import java.util.Map;

/**
 * Living entities class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class LivingEntity extends GameEntity {

    protected int damage = 0;
    protected GameBehavior currentBehavior = GameBehavior.IDLE;

    //Animations given Behavior and Direction
    protected Map<GameBehavior, Map<Direction, Animation>> animations = new HashMap<>();

    public LivingEntity(Texture texture) {
        super(texture);
        this.currentDirection = Direction.DOWN;
    }

    /**
     * Method for hurting the LivingEntity
     *
     * @param damageReceived to be subtracted
     */
    public void hurt(int damageReceived) {
        this.damage += Math.min(this.getResistance(), damageReceived);
    }

    /**
     * Handles the LivingEntity game logic
     */
    public abstract void logic();

    /**
     * gets the Entity Resistance
     */
    public abstract int getResistance();
}
