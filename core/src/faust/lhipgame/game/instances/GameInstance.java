package faust.lhipgame.game.instances;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.gameentities.GameEntity;

import java.util.List;
import java.util.Objects;

/**
 * Entity instanced in game world class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class GameInstance {

    protected final static int POSITION_OFFSET = 16;
    protected final static int POSITION_Y_OFFSET = 8;

    // Flicker effect variables
    protected boolean mustFlicker = false;// flag that is true when the Instance must be hidden
    protected long startToFlickTime = 0;

    protected GameEntity entity;
    protected Body body;
    protected float startX = LHIPGame.GAME_WIDTH / 2;
    protected float startY = LHIPGame.GAME_HEIGHT / 3;

    protected ParticleEmitter particleEmitter;

    public GameInstance(GameEntity entity) {
        Objects.requireNonNull(entity);

        this.entity = entity;
        this.particleEmitter = new ParticleEmitter();
    }

    /**
     * Inits the BodyDefinition
     */
    public abstract void createBody(final World world, float x, float y);

    public abstract void draw(final SpriteBatch batch, float stateTime);

    public Body getBody() {
        return body;
    }

    /**
     * Disposing internal resources
     */
    public void dispose() {
        this.body.getFixtureList().forEach(f ->
                this.body.destroyFixture(f));
    }

    /**
     * Returs the nearest Instance from this in the room. USE ONLY AFTER INSERTING THE POI IN THE WORLD
     *
     * @return the nearest Instance from this in the room
     */
    protected GameInstance getNearestInstance(List<GameInstance> instanceList) {

        GameInstance nearest = null;

        for (GameInstance poi : instanceList) {
            // In no nearest, just return the first one
            if (Objects.isNull(nearest)) {
                nearest = poi;
            } else if (nearest.getBody().getPosition().dst(getBody().getPosition()) > poi.getBody().getPosition().dst(getBody().getPosition())) {
                nearest = poi;
            }
        }
        return nearest;

    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }
}
