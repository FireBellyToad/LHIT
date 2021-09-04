package faust.lhitgame.game.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

/**
 * Single sprite Entities class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class SpriteEntity extends GameEntity {

    protected static final float FRAME_DURATION = 0.1f;

    protected Animation animation;

    public SpriteEntity(Texture texture) {
        super(texture);

        this.initAnimation(0);
    }

    public SpriteEntity(Texture texture, int rowNumber) {
        super(texture);

        this.initAnimation(rowNumber);
    }

    /**
     * Initializes the animation
     */
    protected void initAnimation(int rowNumber) {
        TextureRegion[] frames = Arrays.copyOfRange(getFramesFromTexture(), getTextureColumns() * (rowNumber), getTextureColumns() * (rowNumber + 1));

        this.animation = new Animation<>(FRAME_DURATION, frames);
    }

    /**
     * Returns frame to render
     *
     * @param stateTime state time to render
     * @return the TextureRegion of the frame
     */
    public TextureRegion getFrame(float stateTime) {
        return (TextureRegion) animation.getKeyFrame(stateTime, true);
    }

}
