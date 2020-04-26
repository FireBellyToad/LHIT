package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.Arrays;

public class StrixEntity extends LivingEntity {

    public StrixEntity() {
        super(new Texture("sprites/strix_sheet.png"));
    }

    @Override
    public int getResistance() {
        return 10;
    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFramesLeft = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] idleFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns() * 2);
        TextureRegion[] walkFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns() * 2, getTextureColumns() * 3);
        TextureRegion[] walkFramesRight = Arrays.copyOfRange(allFrames, getTextureColumns() * 3, getTextureColumns() * 4);

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesLeft), GameBehavior.IDLE, Direction.DOWN);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesLeft), GameBehavior.IDLE, Direction.LEFT);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, Direction.UP);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, Direction.RIGHT);

        // Initialize the Walk Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesLeft), GameBehavior.WALK, Direction.DOWN);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesLeft), GameBehavior.WALK, Direction.LEFT);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesRight), GameBehavior.WALK, Direction.UP);
        addAnimationForDirection(new Animation<TextureRegion>(FRAME_DURATION, walkFramesRight), GameBehavior.WALK, Direction.RIGHT);

    }

    @Override
    protected int getTextureColumns() {
        return 4;
    }

    @Override
    protected int getTextureRows() {
        return 4;
    }
}
