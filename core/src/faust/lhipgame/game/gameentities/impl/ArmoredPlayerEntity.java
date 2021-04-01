package faust.lhipgame.game.gameentities.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;

import java.util.Arrays;

/**
 * Armored Player entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class ArmoredPlayerEntity extends AnimatedEntity {

    private Texture shadow;

    public ArmoredPlayerEntity(AssetManager assetManager) {
        super(assetManager.get("sprites/walfrit_armored_sheet.png"));
        shadow = assetManager.get("sprites/shadow.png");
    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFramesDown = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] idleFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns() * 2);
        TextureRegion[] idleFramesUp = Arrays.copyOfRange(allFrames, getTextureColumns() * 2, getTextureColumns() * 3);
        TextureRegion[] idleFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns() * 3, getTextureColumns() * 4);
        TextureRegion[] walkFramesDown = Arrays.copyOfRange(allFrames, getTextureColumns() * 4, getTextureColumns() * 5);
        TextureRegion[] walkFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns() * 5, getTextureColumns() * 6);
        TextureRegion[] walkFramesUp = Arrays.copyOfRange(allFrames, getTextureColumns() * 6, getTextureColumns() * 7);
        TextureRegion[] walkFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns() * 7, getTextureColumns() * 8);
        TextureRegion[] attackFramesDown = Arrays.copyOfRange(allFrames, getTextureColumns() * 8, getTextureColumns() * 9);
        TextureRegion[] attackFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns() * 9, getTextureColumns() * 10);
        TextureRegion[] attackFramesUp = Arrays.copyOfRange(allFrames, getTextureColumns() * 10, getTextureColumns() * 11);
        TextureRegion[] attackFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns() * 11, getTextureColumns() * 12);
        TextureRegion[] kneeFrames = Arrays.copyOfRange(allFrames, getTextureColumns() * 12, getTextureColumns() * 13);

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesDown), GameBehavior.IDLE, Direction.DOWN);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesLeft), GameBehavior.IDLE, Direction.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesUp), GameBehavior.IDLE, Direction.UP);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, Direction.RIGHT);

        // Initialize the Walk Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesDown), GameBehavior.WALK, Direction.DOWN);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesLeft), GameBehavior.WALK, Direction.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesUp), GameBehavior.WALK, Direction.UP);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesRight), GameBehavior.WALK, Direction.RIGHT);

        // Initialize the Walk Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, attackFramesDown), GameBehavior.ATTACK, Direction.DOWN);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, attackFramesLeft), GameBehavior.ATTACK, Direction.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, attackFramesUp), GameBehavior.ATTACK, Direction.UP);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, attackFramesRight), GameBehavior.ATTACK, Direction.RIGHT);

        // Initialize the Hurt Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesDown), GameBehavior.HURT, Direction.DOWN);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesLeft), GameBehavior.HURT, Direction.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesUp), GameBehavior.HURT, Direction.UP);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, walkFramesRight), GameBehavior.HURT, Direction.RIGHT);

        // Initialize the Knee Animation with the frame interval and array of frames
        addAnimation(new Animation<>(FRAME_DURATION, kneeFrames), GameBehavior.KNEE);


    }

    @Override
    protected int getTextureColumns() {
        return 12;
    }

    @Override
    protected int getTextureRows() {
        return 13;
    }

}
