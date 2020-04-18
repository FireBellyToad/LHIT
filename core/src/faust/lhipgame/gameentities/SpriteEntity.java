package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.enums.DecorationsEnum;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.*;

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

    public SpriteEntity(Texture texture,int rowNumber) {
        super(texture);

        this.initAnimation(rowNumber);
    }

    /**
     * Initializes the animation
     */
    protected abstract void initAnimation(int rowNumber);

    /**
     * Returns frame to render
     *
     * @param stateTime state time to render
     * @return the TextureRegion of the frame
     */
    public TextureRegion getFrame(float stateTime) {
        return  (TextureRegion) animation.getKeyFrame(stateTime, true);
    }

}
