package faust.lhipgame.gameentities;

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
    public boolean isDead();

}
