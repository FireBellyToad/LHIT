package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.HashMap;

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
        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(this.texture,
                this.texture.getWidth() / getTextureColumns(),
                this.texture.getHeight() / getTextureRows());

        // Place the regions into a 1D array in the correct order, starting from the top 
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] idleFrames = new TextureRegion[getTextureColumns() * getTextureRows()];
        int index = 0;
        for (int i = 0; i < getTextureRows(); i++) {
            for (int j = 0; j < getTextureColumns(); j++) {
                idleFrames[index++] = tmp[i][j];
            }
        }

        // Initialize the Animation with the frame interval and array of frames
        addAnimation(new Animation<TextureRegion>(0.025f, idleFrames),GameBehavior.IDLE);


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
