package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;

/**
 * Living entities class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class LivingEntity extends AnimatedEntity {

    public LivingEntity(Texture texture) {
        super(texture);
    }

    /**
     * gets the Entity Resistance
     */
    public abstract int getResistance();

}
