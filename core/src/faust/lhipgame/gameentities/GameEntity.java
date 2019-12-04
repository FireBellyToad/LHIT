package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.sun.tools.javac.util.Assert;
import faust.lhipgame.gameentities.enums.Direction;

import java.util.Objects;

/**
 * Game entities class
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class GameEntity {

    protected Texture texture;

    public GameEntity(Texture texture) {
        Objects.requireNonNull(texture);

        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Disposing internal resources
     */
    public void dispose(){
        this.texture.dispose();
    };
}
