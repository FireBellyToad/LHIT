package faust.lhipgame.game.gameentities;

/**
 * Interface for describing attacking behaviour
 */
public interface Attacker {
    /**
     * @return a random damage roll INFLICTED BY the Killable
     */
    double damageRoll();

}
