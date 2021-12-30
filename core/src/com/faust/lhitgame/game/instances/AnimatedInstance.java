package com.faust.lhitgame.game.instances;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.faust.lhitgame.game.gameentities.GameEntity;
import com.faust.lhitgame.game.gameentities.enums.DirectionEnum;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.rooms.AbstractRoom;

import java.util.Objects;

public abstract class AnimatedInstance extends GameInstance {

    protected static final int LINE_OF_SIGHT = 60;
    protected int damage = 0;

    protected GameBehavior currentBehavior = GameBehavior.IDLE;
    protected DirectionEnum currentDirectionEnum = DirectionEnum.UNUSED;

    protected Body hitBox;

    public AnimatedInstance(final GameEntity entity) {
        super(entity);
    }

    /**
     * Handles the LivingEntity game logic
     * @param stateTime
     * @param currentRoom
     */
    public abstract void doLogic(float stateTime, AbstractRoom currentRoom);

    /**
     * Utility for extracting Direction from a directionNormal normal
     */
    protected DirectionEnum extractDirectionFromNormal(Vector2 directionNormal) {

        if (directionNormal.x <= -0.5) {
            return DirectionEnum.LEFT;
        } else if (directionNormal.x > 0.5) {
            return DirectionEnum.RIGHT;
        }

        if (directionNormal.y < 0) {
            return DirectionEnum.DOWN;
        } else {
            return DirectionEnum.UP;
        }
    }

    @Override
    public void dispose() {
        if(!Objects.isNull(hitBox)){
            this.hitBox.getFixtureList().forEach(f ->
                    hitBox.destroyFixture(f));
        }
        super.dispose();
    }

    public GameBehavior getCurrentBehavior() {
        return currentBehavior;
    }

    /**
     * Alter state time for different animation speed based on current behaviour
     *
     * @param stateTime
     * @return
     */
    protected abstract float mapStateTimeFromBehaviour(float stateTime);

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
