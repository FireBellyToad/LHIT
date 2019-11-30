package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.enums.Direction;

/**
 * Entity instanced in game world class
 * @author Jacopo "Faust" Buttiglieri
 */
public class GameInstance {

    protected GameEntity entity;
    protected Body body;
    protected Direction currentDirection = Direction.UNUSED;

    //TODO parametrize
    public GameInstance(GameEntity entity) {
        this.entity = entity;
    }

    /**
     * Inits the BodyDefinition TODO Rivedere
     */
    public void createBody(World world, int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x,y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

    }

    /**
     * Draw the Entity using Body position
     * @param batch
     */
    public void draw(SpriteBatch batch){
        System.out.println(body.getPosition().toString());
        batch.draw(entity.getTexture(), body.getPosition().x, body.getPosition().y);
    };

    public Body getBody() {
        return body;
    }

    /**
     * Disposing internal resources
     */
    public void dispose() {
        this.entity.dispose();
    }
}
