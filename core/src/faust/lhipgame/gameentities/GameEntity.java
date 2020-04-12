package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;

import java.util.Objects;

/**
 * Game entities class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class GameEntity {

    protected Texture texture;

    public GameEntity(Texture texture) {
        Objects.requireNonNull(texture);

        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    /**
     * Disposing internal resources
     */
    public void dispose() {
        this.texture.dispose();
    }

    ;
}
