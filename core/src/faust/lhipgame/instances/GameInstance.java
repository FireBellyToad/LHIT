package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.sun.tools.javac.util.Assert;
import faust.lhipgame.gameentities.GameEntity;

import java.util.List;
import java.util.Objects;

/**
 * Entity instanced in game world class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class GameInstance {

    protected GameEntity entity;
    protected Body body;

    public GameInstance(GameEntity entity) {
        Objects.requireNonNull(entity);

        this.entity = entity;
    }

    /**
     * Inits the BodyDefinition TODO Rivedere
     */
    public void createBody(final World world, float x, float y, final boolean isStaticBody) {
        Objects.requireNonNull(world);

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
        * @param stateTime
        */
    public void draw(final SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);

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

    /**
     * @return the nearest Instance from this in the room
     */
    protected GameInstance getNearestInstance(List<GameInstance> instanceList) {

        GameInstance nearest = null;

        for (GameInstance poi : instanceList) {
            // In no nearest, just return the first one
            if (Objects.isNull(nearest)) {
                nearest = poi;
            } else if (nearest.getBody().getPosition().dst(getBody().getPosition()) <= poi.getBody().getPosition().dst(getBody().getPosition())) {
                nearest = poi;
            }
        }
        return nearest;

    }
}
