package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.gameentities.enums.Direction;

/**
 * Game entities class
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class GameEntity {

    protected Texture texture;
    protected Direction currentDirection = Direction.UNUSED;

    public GameEntity(Texture texture) {
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
