package faust.lhipgame.instances;

import com.badlogic.gdx.physics.box2d.*;

import java.awt.*;
import java.util.Objects;

/**
 * Static invisible Wall instance. Doesn't need anything from GameInstance
 * @author Jacopo "Faust" Buttiglieri
 */
public class WallInstance {

    private Body body;
    private Rectangle wallRect;

    public WallInstance(float x,float y,float w,float h) {
        this.wallRect = new Rectangle((int) x,(int) y,(int) w,(int) h);
    }

    public Body getBody() {
        return body;
    }
    public Rectangle getWallRect() {
        return wallRect;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void dispose() {
        this.body.getFixtureList().forEach(f ->
                this.body.destroyFixture(f));
    }

    public void createBody(final World world) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(wallRect.width / 2, wallRect.height / 2);
        bodyDef.position.set(wallRect.x + wallRect.width / 2, wallRect.y + wallRect.height / 2);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density =  1;
        fixtureDef.friction =  1;
        fixtureDef.isSensor = false;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }
}
