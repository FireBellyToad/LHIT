package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.Arrays;

/**
 * Player class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class PlayerEntity extends LivingEntity {

    public PlayerEntity() {
        super(new Texture("walfrit_sheet.png"));
    }

    @Override
    public void logic() {
        //TODO logic
    }

    @Override
    public int getResistance() {
        return 12;
    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFramesDown = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] idleFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns()*2);
        TextureRegion[] idleFramesUp = Arrays.copyOfRange(allFrames, getTextureColumns()*2, getTextureColumns()*3);
        TextureRegion[] idleFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns()*3, getTextureColumns()*4);

        // Initialize the Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesDown), GameBehavior.IDLE, Direction.DOWN);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesLeft), GameBehavior.IDLE, Direction.LEFT);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesUp), GameBehavior.IDLE, Direction.UP);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, Direction.RIGHT);


    }

    @Override
    protected int getTextureColumns() {
        return 12;
    }

    @Override
    protected int getTextureRows() {
        return 4;
    }
}
