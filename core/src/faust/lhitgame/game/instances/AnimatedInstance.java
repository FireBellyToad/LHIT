package faust.lhitgame.game.instances;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import faust.lhitgame.game.gameentities.GameEntity;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.rooms.RoomContent;

import java.util.Objects;

/**
 * Animated entity instanced in game world class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AnimatedInstance extends GameInstance {

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
     * @param roomContent
     */
    public abstract void doLogic(float stateTime, RoomContent roomContent);

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

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
