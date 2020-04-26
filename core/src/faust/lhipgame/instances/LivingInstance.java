package faust.lhipgame.instances;

import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

public abstract class LivingInstance extends GameInstance {

    protected static final int LINE_OF_SIGHT = 60;

    protected GameBehavior currentBehavior = GameBehavior.IDLE;
    protected Direction currentDirection = Direction.UNUSED;

    public LivingInstance(final GameEntity entity) {
        super(entity);
    }

    /**
     * Handles the LivingEntity game logic
     */
    public abstract void doLogic();
}
