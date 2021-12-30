package com.faust.lhitgame.game.gameentities.interfaces;

/**
 * Interface for describing dying behaviour
 *
 * @author Jacopo "Faust" Buttiglieri
 */
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
