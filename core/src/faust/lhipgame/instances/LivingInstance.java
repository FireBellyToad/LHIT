package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sun.tools.javac.util.Assert;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.Objects;

public abstract class LivingInstance extends GameInstance {

    protected GameBehavior currentBehavior = GameBehavior.IDLE;
    protected Direction currentDirection = Direction.UNUSED;

    public LivingInstance(final GameEntity entity) {
        super(entity);
    }

    /**
     * Draw the Entity frames using Body position
     *
     * @param batch
     * @param stateTime
     */
    @Override
    public void draw(final SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);

        TextureRegion frame = ((LivingEntity) entity).getFrame(currentBehavior, currentDirection, stateTime);
        batch.draw(frame, body.getPosition().x, body.getPosition().y);
    }

    /**
     * Handles the LivingEntity game logic
     */
    public abstract void logic();
}
