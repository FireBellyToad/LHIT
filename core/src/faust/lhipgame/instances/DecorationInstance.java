package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.DecorationEntity;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.LivingEntity;

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
        batch.draw(entity.getTexture(), body.getPosition().x, body.getPosition().y);
    }
}
