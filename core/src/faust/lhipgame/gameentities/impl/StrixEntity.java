package faust.lhipgame.gameentities.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.Arrays;

/**
 *
 * Strix enemy Entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class StrixEntity extends LivingEntity {

    private Texture shadow;

    public StrixEntity() {
        super(new Texture("sprites/strix_sheet.png"));
        shadow = new Texture("sprites/shadow.png");
    }

    @Override
    public int getResistance() {
        return 10;
    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFramesDown = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] idleFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns()*2);
        TextureRegion[] idleFramesUp = Arrays.copyOfRange(allFrames, getTextureColumns()*2, getTextureColumns() * 3);
        TextureRegion[] idleFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns()*3, getTextureColumns() * 4);
        TextureRegion[] walkFramesDown = Arrays.copyOfRange(allFrames, getTextureColumns() * 4, getTextureColumns() * 5);
        TextureRegion[] walkFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns() * 5, getTextureColumns() * 6);
        TextureRegion[] walkFramesUp = Arrays.copyOfRange(allFrames, getTextureColumns() * 6, getTextureColumns() * 7);
        TextureRegion[] walkFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns() * 7, getTextureColumns() * 8);
        TextureRegion[] attachedFrames = Arrays.copyOfRange(allFrames, getTextureColumns() * 8, getTextureColumns() * 9);

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesDown), GameBehavior.IDLE, Direction.DOWN);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesLeft), GameBehavior.IDLE, Direction.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesUp), GameBehavior.IDLE, Direction.UP);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, Direction.RIGHT);

        // Initialize the Walk Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesDown), GameBehavior.WALK, Direction.DOWN);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesLeft), GameBehavior.WALK, Direction.LEFT);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesUp), GameBehavior.WALK, Direction.UP);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesRight), GameBehavior.WALK, Direction.RIGHT);

        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, attachedFrames), GameBehavior.ATTACK, Direction.UNUSED);

        // Initialize the Walk Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesDown), GameBehavior.HURT, Direction.DOWN);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesLeft), GameBehavior.HURT, Direction.LEFT);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesUp), GameBehavior.HURT, Direction.UP);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesRight), GameBehavior.HURT, Direction.RIGHT);

    }

    @Override
    protected int getTextureColumns() {
        return 4;
    }

    @Override
    protected int getTextureRows() {
        return 9;
    }

    public Texture getShadowTexture() {
        return shadow;
    }
}
