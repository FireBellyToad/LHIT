package faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhitgame.LHITGame;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.impl.PortalEntity;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.rooms.RoomContent;

import java.util.Objects;

/**
 * @author Portal Instance class
 */
public class PortalInstance extends AnimatedInstance {

    public PortalInstance(AssetManager assetManager) {
        super(new PortalEntity(assetManager));
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {
        //Nothing to do here...
    }

    private float mapStateTimeFromBehaviour(float stateTime) {
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
        batch.draw(frame, 80 - POSITION_OFFSET , LHITGame.GAME_HEIGHT- POSITION_OFFSET - 108);
        batch.end();
    }
}
