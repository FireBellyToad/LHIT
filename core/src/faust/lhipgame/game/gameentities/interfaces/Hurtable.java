package faust.lhipgame.game.gameentities.interfaces;

import faust.lhipgame.game.instances.GameInstance;

/**
 * Interface for describing hurting and dying behaviour
 */
public interface Hurtable {
    /**
     *
     * @return the current resitance
     */
    int  getResistance();

    /**
     *
     * @param attacker
     */
    void hurt(GameInstance attacker);

    /**
     * Logic to be done after being hurt
     */
    void postHurtLogic(GameInstance attacker);

    /**
     * @return true if the damage is greater or equal than the resitance
     */
    boolean isDying();

    /**
     * @return true if the damage is really dead
     */
    boolean isDead();

}
