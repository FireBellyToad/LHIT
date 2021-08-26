package faust.lhipgame.game.instances.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.PortalEntity;
import faust.lhipgame.game.instances.AnimatedInstance;

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
        batch.begin();
        batch.draw(((PortalEntity) entity).getFrame(GameBehavior.IDLE, stateTime), 80 - POSITION_OFFSET , LHIPGame.GAME_HEIGHT- POSITION_OFFSET - 108);
        batch.end();
    }
}
