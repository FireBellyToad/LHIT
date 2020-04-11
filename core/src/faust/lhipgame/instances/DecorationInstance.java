package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhipgame.gameentities.DecorationEntity;

import java.util.Objects;

public class DecorationInstance extends GameInstance {

    public DecorationInstance(float x, float y) {
        //TODO rivedere
        super(new DecorationEntity());
        this.startX = x;
        this.startY = y;
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        batch.draw(entity.getTexture(), body.getPosition().x- POSITION_OFFSET, body.getPosition().y- POSITION_OFFSET);
    }
}
