package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.gameentities.DecorationEntity;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.gameentities.enums.DecorationsEnum;

import java.util.Objects;

public class DecorationInstance extends GameInstance {

    private DecorationsEnum type;
    private boolean interacted = false;

    public DecorationInstance(float x, float y, DecorationsEnum type) {
        super(new DecorationEntity(type));
        this.startX = x;
        this.startY = y;
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);

        TextureRegion frame;

        if (interacted) {
            frame = ((SpriteEntity) entity).getFrame(GameEntity.FRAME_DURATION);
        } else {
            frame = ((SpriteEntity) entity).getFrame(0);
        }

        //Rivedere
        batch.draw(frame, body.getPosition().x + calculateAdditionalOffset() - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);
    }

    private int calculateAdditionalOffset() {

        if (DecorationsEnum.CROSS_IRON.equals(((DecorationEntity) entity).getType())) {
            return 2;
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
        switch (((DecorationEntity) entity).getType()){
            case CROSS_IRON:{
                shape.setAsBox(2, 2);
                bodyDef.position.set(x - 2, y - 16);
                break;
            }
            case PLANT:{
                shape.setAsBox(5, 3);
                bodyDef.position.set(x, y - 16);
                break;
            }
            default:{
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

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public boolean isPassable() {
        return DecorationsEnum.PLANT.equals(((DecorationEntity) entity).getType());
    }

    public void setInteracted(boolean interacted) {
        this.interacted = interacted;
    }

    public boolean getInteracted(){
        return this.interacted;
    }
}
