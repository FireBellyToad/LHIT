package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

        TextureRegion[] idleFrames = Arrays.copyOfRange(allFrames, 0, getTextureColumns());

        // Initialize the Animation with the frame interval and array of frames
        addAnimation(new Animation<TextureRegion>(FRAME_DURATION, idleFrames), GameBehavior.IDLE);


    }

    @Override
    protected int getTextureColumns() {
        return 12;
    }

    @Override
    protected int getTextureRows() {
        return 1;
    }
}
