package com.faust.lhengine.game.instances.interfaces;

import com.faust.lhengine.game.instances.GameInstance;

/**
 * Interface for describing hurting behaviour from a Damager GameInstance
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public interface Hurtable <T extends GameInstance & Damager>extends Killable {
    /**
     *
     * @return the current resitance
     */
    int  getResistance();

    /**
     *
     * @param attacker
     */
    void hurt(T attacker);

    /**
     * Logic to be done after being hurt
     */
    void postHurtLogic(T attacker);

}
