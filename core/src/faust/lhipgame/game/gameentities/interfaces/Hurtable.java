package faust.lhipgame.game.gameentities.interfaces;

import faust.lhipgame.game.instances.GameInstance;

/**
 * Interface for describing hurting behaviour
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
