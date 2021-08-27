package faust.lhipgame.game.instances.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.PortalEntity;
import faust.lhipgame.game.instances.AnimatedInstance;

import java.util.Objects;

/**
 * @author Portal Instance class
 */
public class PortalInstance extends AnimatedInstance {

    public PortalInstance(AssetManager assetManager) {
        super(new PortalEntity(assetManager));
    }

    @Override
    public void doLogic(float stateTime) {
        //Nothing to do here...
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {
        return stateTime;
    }

    @Override
    public void createBody(World world, float x, float y) {
        //not needed!
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        //is fixed;
        Objects.requireNonNull(batch);
        batch.begin();
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, mapStateTimeFromBehaviour(stateTime), true);
        batch.draw(frame, 80 - POSITION_OFFSET , LHIPGame.GAME_HEIGHT- POSITION_OFFSET - 108);
        batch.end();
    }
}
