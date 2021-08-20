package faust.lhipgame.cutscenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.GameEntity;
import faust.lhipgame.game.gameentities.SpriteEntity;
import faust.lhipgame.game.gameentities.enums.DirectionEnum;
import faust.lhipgame.game.gameentities.enums.GameBehavior;

import java.util.Objects;

/**
 * Actor for using GameEntities outside of Game scope with animation handling. Used usually in cutscenes
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SimpleActor {

    private final GameEntity entity;
    private final Vector2 position;
    private final GameBehavior currentBehavior;
    private final DirectionEnum direction;

    public SimpleActor(GameEntity entity, GameBehavior currentBehavior, DirectionEnum direction, float x, float y) {
        this.entity = entity;
        this.currentBehavior = currentBehavior;
        this.direction = direction;
        this.position = new Vector2(x, y);
    }

    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        TextureRegion frame = null;

        if (entity instanceof AnimatedEntity) {
            frame = ((AnimatedEntity) entity).getFrame(currentBehavior, direction, stateTime, true);
        } else if (entity instanceof SpriteEntity) {
            frame = ((SpriteEntity) entity).getFrame(0);
        }

        Objects.requireNonNull(frame);

        batch.begin();
        batch.draw(frame, position.x - 16, position.y-8);
        batch.end();
    }

}
