package faust.lhipgame.game.gameentities;

import faust.lhipgame.game.instances.GameInstance;

/**
 * Interface for describing hurting and dying behaviour
 */
public interface Attacker {
    /**
     * @return a random damage roll INFLICTED BY the Killable
     */
    double damageRoll();

}
