package faust.lhipgame.game.gameentities;

/**
 * Interface for describing hurting and dying behaviour
 */
public interface Killable {
    /**
     *
     * @return the current resitance
     */
    int  getResistance();

    /**
     *
     * @param damageReceived
     */
    void hurt(int damageReceived);

    /**
     * Logic to be done after being hurt
     */
    void postHurtLogic();

    /**
     * @return true if the damage is greater or equal than the resitance
     */
    boolean isDying();

    /**
     * @return true if the damage is really dead
     */
    boolean isDead();

}
