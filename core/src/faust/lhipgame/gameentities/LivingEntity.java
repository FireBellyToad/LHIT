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

    protected static final float FRAME_DURATION = 0.1f;

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
     * gets the Entity Resistance
     */
    public abstract int getResistance();

    /**
     * @return true if the damage is greater or equal than the resitance
     */
    public boolean isDead() {
        return this.damage >= this.getResistance();
    }

    /**
     * Initializes the animation
     */
    protected abstract void initAnimations();

    /**
     * @return the columns of the texture of the whole spritesheet
     */
    protected abstract int getTextureColumns();

    /**
     * @return the rows of the texture of the whole spritesheet
     */
    protected abstract int getTextureRows();

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
     * Returns frame to render
     *
     * @param behavior  The behavior of the animation
     * @param direction the direction of the animation
     * @param stateTime state time to render
     * @return the TextureRegion of the frame
     */
    public TextureRegion getFrame(GameBehavior behavior, Direction direction, float stateTime) {
        Objects.requireNonNull(behavior);
        Objects.requireNonNull(direction);

        // Get all animations for behaviour
        Map<Direction, Animation> behaviorAnimations = this.animations.get(behavior);

        Objects.requireNonNull(behaviorAnimations);

        TextureRegion toReturn;
        // If the animations doesn't have any direction, then return the unused one
        if (behaviorAnimations.containsKey(Direction.UNUSED)) {
            toReturn = (TextureRegion) behaviorAnimations.get(Direction.UNUSED).getKeyFrame(stateTime, true);
        } else {
            toReturn = (TextureRegion) behaviorAnimations.get(direction).getKeyFrame(stateTime, true);
        }

        Objects.requireNonNull(toReturn);

        return toReturn;
    }

    protected TextureRegion[] getFramesFromTexture(){
        // Use the split utility method to create a 2D array of TextureRegions.
        // The sprite sheet MUST contain frames of equal size and they MUST be
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(this.texture,
                this.texture.getWidth() / getTextureColumns(),
                this.texture.getHeight() / getTextureRows());

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] allFrames = new TextureRegion[getTextureColumns() * getTextureRows()];
        int index = 0;
        for (int i = 0; i < getTextureRows(); i++) {
            for (int j = 0; j < getTextureColumns(); j++) {
                allFrames[index++] = tmp[i][j];
            }
        }

        return allFrames;
    }
}
