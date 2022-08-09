package com.faust.lhengine.game.instances.interfaces;

import com.faust.lhengine.game.instances.GameInstance;

/**
 * Interface for describing hurting behaviour
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public interface Hurtable extends Killable {
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

}
