package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;

/**
 * Player class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Player extends LivingEntity {

    public static final int MAX_RESISTANCE = 12;

    public Player() {
        super(new Texture("badlogic.jpg"));
        this.resistance = MAX_RESISTANCE;
    }

    @Override
    public void logic() {
        //TODO logic
    }
}
