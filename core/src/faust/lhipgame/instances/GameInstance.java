package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.sun.tools.javac.util.Assert;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.enums.Direction;

/**
 * Entity instanced in game world class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class GameInstance {

    protected GameEntity entity;
    protected Body body;
    protected Direction currentDirection = Direction.UNUSED;

    public GameInstance(GameEntity entity) {
        Assert.checkNonNull(entity);

        this.entity = entity;
    }

    /**
     * Inits the BodyDefinition TODO Rivedere
     */
    public void createBody(final World world, int x, int y, final boolean isStaticBody) {
        Assert.checkNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStaticBody ? BodyDef.BodyType.StaticBody: BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    /**
     * Draw the Entity using Body position
     *
     * @param batch
     */
    public void draw(SpriteBatch batch) {
        Assert.checkNonNull(batch);

        batch.draw(entity.getTexture(), body.getPosition().x, body.getPosition().y);
    }

    ;

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
