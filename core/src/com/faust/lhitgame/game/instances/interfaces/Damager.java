package com.faust.lhitgame.game.instances.interfaces;

/**
 * Interface for describing attacking behaviour
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public interface Damager {
    /**
     * @return a random damage roll INFLICTED BY the Killable
     */
    double damageRoll();

}
