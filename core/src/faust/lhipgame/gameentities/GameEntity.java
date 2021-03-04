package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Objects;

/**
 * Game entities class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class GameEntity {

    public static final float FRAME_DURATION = 0.1f;

    protected Texture texture;

    public GameEntity(Texture texture) {
        Objects.requireNonNull(texture);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    protected TextureRegion[] getFramesFromTexture() {
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

    /**
     * @return the columns of the texture of the whole spritesheet
     */
    protected abstract int getTextureColumns();

    /**
     * @return the rows of the texture of the whole spritesheet
     */
    protected abstract int getTextureRows();

}
