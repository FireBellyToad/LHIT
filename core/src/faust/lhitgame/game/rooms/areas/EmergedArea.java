package faust.lhitgame.game.rooms.areas;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Objects;

/**
 * Static invisible emerged land area.
 * @author Jacopo "Faust" Buttiglieri
 */
public class EmergedArea {

    private Body body;
    private final PolygonShape polygonShape;
    private final float x;
    private final float y;

    public EmergedArea(Polygon polygon) {
        this.polygonShape = new PolygonShape();
        x = polygon.getX();
        y = polygon.getY();
        this.polygonShape.set(polygon.getVertices());
    }

    public Body getBody() {
        return body;
    }

    public void dispose() {
        this.body.getFixtureList().forEach(f ->
                this.body.destroyFixture(f));
    }

    public void createBody(final World world) {
        Objects.requireNonNull(world);

        Vector2 firstVertex = new Vector2();
        this.polygonShape.getVertex(0,firstVertex);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x,y);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density =  0;
        fixtureDef.friction =  0;
        fixtureDef.isSensor = true;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        polygonShape.dispose();
    }
}
