package faust.lhipgame.cutscenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.GameEntity;
import faust.lhipgame.game.gameentities.SpriteEntity;
import faust.lhipgame.game.gameentities.enums.DirectionEnum;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.PlayerEntity;
import faust.lhipgame.utils.ShaderWrapper;

import java.util.*;

/**
 * Actor for using GameEntities outside of Game scope with animation handling. Used usually in cutscenes
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SimpleActor {

    private final boolean isShaded;
    private final GameEntity entity;
    private final Vector2 position;
    private final GameBehavior currentBehavior;
    private final DirectionEnum direction;
    private final List<SimpleActorParametersEnum> params;

    /**
     * Costructor
     *
     *  @param entity
     * @param currentBehavior
     * @param direction
     * @param x
     * @param y
     * @param isShaded
     * @param params
     */
    public SimpleActor(GameEntity entity, GameBehavior currentBehavior, DirectionEnum direction, float x, float y, boolean isShaded, List<SimpleActorParametersEnum> params) {
        this.entity = entity;
        this.currentBehavior = currentBehavior;
        this.direction = direction;
        this.position = new Vector2(x, y);
        this.isShaded = isShaded;
        this.params = params;
    }

    /**
     * Draws the actor
     *
     * @param batch
     * @param stateTime
     */
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        TextureRegion frame = null;

        ShaderWrapper shader = null;
        if(isShaded){
            shader = setShaderOn(batch);
        }

        if (entity instanceof AnimatedEntity) {
            frame = ((AnimatedEntity) entity).getFrame(currentBehavior, direction, stateTime, true);
        } else if (entity instanceof SpriteEntity) {
            frame = ((SpriteEntity) entity).getFrame(0);
        }

        Objects.requireNonNull(frame);

        batch.begin();
        batch.draw(frame, position.x - 16, position.y-8);
        batch.end();

        if(isShaded){
            //Restore default shader
            shader.resetDefaultShader(batch);
        }
    }

    /**
     * Activate shader
     * @return the activated shader
     * @param batch
     */
    private ShaderWrapper setShaderOn(SpriteBatch batch) {
        ShaderWrapper shader = null;

        if(entity instanceof PlayerEntity) {
            boolean hasArmor = params.stream().anyMatch(SimpleActorParametersEnum.PLAYER_HAS_ARMOR::equals);
            shader = ((PlayerEntity) entity).getPlayerShader();
            shader.addFlag("hasArmor", hasArmor);
            shader.addFlag("hasHolyLance", true);
            shader.setShaderOnBatchWithFlags(batch);
        }

        Objects.requireNonNull(shader,"Trying to activate a shader without givig a real entity!");

        return shader;
    }

}
