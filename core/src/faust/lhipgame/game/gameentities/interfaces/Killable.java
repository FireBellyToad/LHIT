package faust.lhipgame.game.gameentities.interfaces;

public interface Killable {


    /**
     * @return true if the damage is greater or equal than the resitance
     */
    boolean isDying();

    /**
     * @return true if the damage is really dead
     */
    boolean isDead();
}
