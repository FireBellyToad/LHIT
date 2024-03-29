package com.faust.lhitgame.game.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.game.gameentities.GameEntity;
import com.faust.lhitgame.game.rooms.RoomContent;

import java.util.List;
import java.util.Objects;

/**
 * Entity instanced in game world class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class GameInstance {

    public final static int POSITION_OFFSET = 16;
    public final static int POSITION_Y_OFFSET = 8;

    // Flicker effect variables
    protected boolean mustFlicker = false;// flag that is true when the Instance must be hidden
    protected long startToFlickTime = 0;

    protected final GameEntity entity;
    protected Body body;
    protected float startX = LHITGame.GAME_WIDTH / 2;
    protected float startY = LHITGame.GAME_HEIGHT / 3;
    protected boolean alwaysInBackground = false;

    public GameInstance(GameEntity entity) {
        Objects.requireNonNull(entity);

        this.entity = entity;
    }

    public abstract void doLogic(float stateTime, RoomContent roomContent);

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

    public boolean isDisposable() {
        return false;
    }

    /**
     * When is not moving, adjust position if necessary to avoid pixel tearing
     * @return position with integer x and y
     */
    protected Vector2 adjustPosition() {

        //If moving is not noticeable, go ahead with unadjusted position
        Vector2 velocity = getBody().getLinearVelocity();
        if (velocity.x != 0 || velocity.y != 0) {
            return getBody().getPosition();
        }

        // If stopped, adjust position
        Vector2 pos = getBody().getPosition().cpy();
        pos.x = MathUtils.round(pos.x);
        pos.y = MathUtils.round(pos.y);

        return pos;
    }

    public boolean isAlwaysInBackground() {
        return alwaysInBackground;
    }
}
