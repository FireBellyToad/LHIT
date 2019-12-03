package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sun.tools.javac.util.Assert;
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

    //Animations given Behavior and Direction
    protected Map<GameBehavior, Map<Direction, Animation>> animations = new HashMap<>();

    public LivingEntity(Texture texture) {
        super(texture);

        this.initAnimations();
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

    /**
     *
     * @return true if the damage is greater or equal than the resitance
     */
    public boolean isDead(){
        return this.damage >= this.getResistance();
    }

    /**
     * Initializes the animation
     */
    protected abstract void initAnimations();

    /**
     *
     * @return the columns of the texture of the whole spritesheet
     */
    protected abstract int getTextureColumns();

    /**
     *
     * @return the rows of the texture of the whole spritesheet
     */
    protected abstract int getTextureRows();

    /**
     * Adds a new animation that doesn't use a Direction
     * @param animation the animation itself
     * @param behavior the behaviour associated to the animation
     */
    protected void addAnimation(Animation animation, GameBehavior behavior){
        this.addAnimationForDirection(animation,behavior,Direction.UNUSED);
    }

    /**
     * Adds a new animation
     * @param animation the animation itself
     * @param behavior the behaviour associated to the animation
     * @param direction the direction of the animation
     */
    protected void addAnimationForDirection(Animation animation, GameBehavior behavior, Direction direction){
        Assert.checkNonNull(behavior);
        Assert.checkNonNull(direction);

        if(!this.animations.containsKey(behavior)){
            this.animations.put(behavior,new HashMap<Direction,Animation>());
        }
        this.animations.get(GameBehavior.IDLE).put(direction,animation);
    }

    /**
     * Returns frame to render
     * @param behavior The behavior of the animation
     * @param direction the direction of the animation
     * @param stateTime state time to render
     * @return the TextureRegion of the frame
     */
    public TextureRegion getFrame(GameBehavior behavior, Direction direction, float stateTime){
        Assert.checkNonNull(behavior);
        Assert.checkNonNull(direction);

        Map<Direction, Animation> behaviorAnimations = this.animations.get(behavior);

        Assert.checkNonNull(behaviorAnimations);

        TextureRegion toReturn;
        // If the animations doesn't have any direction, then just return the
        if(behaviorAnimations.containsKey(Direction.UNUSED)){
            toReturn = (TextureRegion) behaviorAnimations.get(Direction.UNUSED).getKeyFrame(stateTime, true);
        } else{
            toReturn = (TextureRegion) behaviorAnimations.get(direction).getKeyFrame(stateTime, true);
        }

        Assert.checkNonNull(toReturn);

        return toReturn;
    };
}
