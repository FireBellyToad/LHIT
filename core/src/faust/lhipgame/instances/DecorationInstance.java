package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.DecorationEntity;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.gameentities.enums.DecorationsEnum;

import java.util.Objects;

public class DecorationInstance extends GameInstance {

    public DecorationInstance(float x, float y, DecorationsEnum type) {
        super(new DecorationEntity(type));
        this.startX = x;
        this.startY = y;
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        TextureRegion frame = ((SpriteEntity) entity).getFrame(stateTime);
        batch.draw(frame, body.getPosition().x- POSITION_OFFSET, body.getPosition().y- POSITION_OFFSET);
    }
}
