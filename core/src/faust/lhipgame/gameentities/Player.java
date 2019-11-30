package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;

/**
 * Player class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Player extends LivingEntity {

    public Player() {
        super(new Texture("badlogic.jpg"));
    }

    @Override
    public void logic() {
        //TODO logic
    }

    @Override
    public int getResistance() {
        return 12;
    }
}
