package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.gameentities.DecorationEntity;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.gameentities.enums.DecorationsEnum;

import java.util.Objects;

public class DecorationInstance extends GameInstance {

    private DecorationsEnum type;

    public DecorationInstance(float x, float y, DecorationsEnum type) {
        super(new DecorationEntity(type));
        this.startX = x;
        this.startY = y;
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        TextureRegion frame = ((SpriteEntity) entity).getFrame(stateTime);
        //Rivedere
        batch.draw(frame, body.getPosition().x + calculateAdditionalOffset() - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);
    }

    private int calculateAdditionalOffset() {

        if(DecorationsEnum.CROSS_IRON.equals(((DecorationEntity) entity).getType())){
            return 2;
        }
        return 0;
    }

    @Override
    public void createBody(final World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.active = isPassable();
        bodyDef.fixedRotation = true;

        // Define shape
        PolygonShape shape = new PolygonShape();

        //Adjustements
        if(DecorationsEnum.CROSS_IRON.equals(((DecorationEntity) entity).getType())){
            shape.setAsBox(2, 2);
            bodyDef.position.set(x-2, y-16);
        }else{
            shape.setAsBox(4, 2);
            bodyDef.position.set(x, y-16);
        }

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 1;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    private boolean isPassable() {
        return !DecorationsEnum.PLANT.equals(((DecorationEntity) entity).getType());
    }

}
