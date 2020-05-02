package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Living entities class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class LivingEntity extends GameEntity {


    //Animations given Behavior and Direction
    protected Map<GameBehavior, Map<Direction, Animation>> animations = new HashMap<>();

    public LivingEntity(Texture texture) {
        super(texture);

        this.initAnimations();
    }

    /**
     * gets the Entity Resistance
     */
    public abstract int getResistance();

    /**
     * Initializes the animation
     */
    protected abstract void initAnimations();

    /**
     * Adds a new animation that doesn't use a Direction
     *
     * @param animation the animation itself
     * @param behavior  the behaviour associated to the animation
     */
    protected void addAnimation(Animation animation, GameBehavior behavior) {
        this.addAnimationForDirection(animation, behavior, Direction.UNUSED);
    }

    /**
     * Adds a new animation
     *
     * @param animation the animation itself
     * @param behavior  the behaviour associated to the animation
     * @param direction the direction of the animation
     */
    protected void addAnimationForDirection(Animation animation, GameBehavior behavior, Direction direction) {
        Objects.requireNonNull(behavior);
        Objects.requireNonNull(direction);

        if (!this.animations.containsKey(behavior)) {
            this.animations.put(behavior, new HashMap<Direction, Animation>());
        }

        this.animations.get(behavior).put(direction, animation);
    }


    /**
     * Returns frame to render with looping animation
     *
     * @param behavior  The behavior of the animation
     * @param direction the direction of the animation
     * @param stateTime state time to render
     * @return the TextureRegion of the frame
     */
    public TextureRegion getFrame(GameBehavior behavior, Direction direction, float stateTime) {
        return getFrame(behavior, direction, stateTime, true);
    }

    /**
     * Returns frame to render
     *
     * @param behavior  The behavior of the animation
     * @param direction the direction of the animation
     * @param stateTime state time to render
     * @param looping   true if must loop
     * @return the TextureRegion of the frame
     */
    public TextureRegion getFrame(GameBehavior behavior, Direction direction, float stateTime, boolean looping) {
        Objects.requireNonNull(behavior);
        Objects.requireNonNull(direction);

        // Get all animations for behaviour
        Map<Direction, Animation> behaviorAnimations = this.animations.get(behavior);

        Objects.requireNonNull(behaviorAnimations);

        TextureRegion toReturn;
        // If the animations doesn't have any direction, then return the unused one
        if (behaviorAnimations.containsKey(Direction.UNUSED)) {
            toReturn = (TextureRegion) behaviorAnimations.get(Direction.UNUSED).getKeyFrame(stateTime, looping);
        } else {
            toReturn = (TextureRegion) behaviorAnimations.get(direction).getKeyFrame(stateTime, looping);
        }

        Objects.requireNonNull(toReturn);

        return toReturn;
    }

    /**
     * Returns current frame Index
     *
     * @param behavior  The behavior of the animation
     * @param direction the direction of the animation
     * @param stateTime state time to render
     * @return true if animation is finished
     */
    public boolean isAnimationFinished(GameBehavior behavior, Direction direction, float stateTime) {
        Objects.requireNonNull(behavior);
        Objects.requireNonNull(direction);

        // Get all animations for behaviour
        Map<Direction, Animation> behaviorAnimations = this.animations.get(behavior);

        Objects.requireNonNull(behaviorAnimations);

        boolean toReturn;
        // If the animations doesn't have any direction, then return the unused one
        if (behaviorAnimations.containsKey(Direction.UNUSED)) {
            toReturn = behaviorAnimations.get(Direction.UNUSED).isAnimationFinished(stateTime);
        } else {
            toReturn = behaviorAnimations.get(direction).isAnimationFinished(stateTime);
        }

        return toReturn;
    }
}
