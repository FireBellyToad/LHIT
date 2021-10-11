package faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhitgame.game.gameentities.GameEntity;
import faust.lhitgame.game.gameentities.SpriteEntity;
import faust.lhitgame.game.gameentities.enums.DecorationsEnum;
import faust.lhitgame.game.gameentities.impl.DecorationEntity;
import faust.lhitgame.game.instances.GameInstance;
import faust.lhitgame.game.instances.interfaces.Interactable;
import faust.lhitgame.game.world.manager.CollisionManager;

import java.util.Objects;

/**
 * Class for Decoration Instances
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class DecorationInstance extends GameInstance implements Interactable {

    private boolean interacted = false;

    public DecorationInstance(float x, float y, DecorationsEnum type, AssetManager assetManager) {
        super(new DecorationEntity(type,assetManager));
        // Add Position Y offset for better position from tiled
        this.startX = x ;
        this.startY = y + POSITION_OFFSET;
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        batch.begin();
        TextureRegion frame;

        if (interacted) {
            frame = ((SpriteEntity) entity).getFrame(GameEntity.FRAME_DURATION);
        } else {
            frame = ((SpriteEntity) entity).getFrame(0);
        }

        //Rivedere
        batch.draw(frame, body.getPosition().x + calculateAdditionalXOffset() - POSITION_OFFSET,
                body.getPosition().y + calculateAdditionalYOffset() - POSITION_Y_OFFSET);
        batch.end();
    }

    /**
     *
     * @return X offset for particular Decoration render
     */
    private int calculateAdditionalXOffset() {

        if (DecorationsEnum.CROSS_IRON.equals(((DecorationEntity) entity).getType())) {
            return 2;
        }
        return 0;
    }

    /**
     *
     * @return Y offset for particular Decoration render
     */
    private int calculateAdditionalYOffset() {

        if (DecorationsEnum.BOAT.equals(((DecorationEntity) entity).getType())) {
            return -4;
        }
        return 0;
    }

    @Override
    public void createBody(final World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        // Define shape
        PolygonShape shape = new PolygonShape();

        //Adjustements
        switch (((DecorationEntity) entity).getType()) {
            case CROSS_IRON: {
                shape.setAsBox(2, 2);
                bodyDef.position.set(x - 2, y - 16);
                break;
            }
            case PLANT: {
                shape.setAsBox(5, 3);
                bodyDef.position.set(x, y - 16);
                break;
            }
            case STONE_1:
            case STONE_2:
            case TREE_STUMP:
            case BOAT:{
                shape.setAsBox(16, 8);
                bodyDef.position.set(x, y - 16);
                break;
            }
            default: {
                shape.setAsBox(4, 2);
                bodyDef.position.set(x, y - 16);
                break;
            }
        }

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = isPassable() ? 0 : 1;
        fixtureDef.friction = isPassable() ? 0 : 1;
        fixtureDef.isSensor = isPassable();
        fixtureDef.filter.categoryBits = CollisionManager.SOLID_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public boolean isPassable() {
        return ((DecorationEntity) entity).isPassable();
    }

    public boolean getInteracted() {
        return this.interacted;
    }

    public DecorationsEnum getType() {
        return ((DecorationEntity) entity).getType();
    }


    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        if( DecorationsEnum.GRASS.equals(getType()) || DecorationsEnum.PLANT.equals(getType()) ){
            ((DecorationEntity) entity).playGrassMove();
        }
        this.interacted = true;
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        this.interacted = false;
    }
}
