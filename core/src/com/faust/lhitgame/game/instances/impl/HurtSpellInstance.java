package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.gameentities.impl.ParticleEffectEntity;
import com.faust.lhitgame.game.gameentities.impl.PlayerEntity;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.interfaces.Damager;
import com.faust.lhitgame.game.instances.interfaces.Interactable;
import com.faust.lhitgame.game.instances.interfaces.Killable;
import com.faust.lhitgame.game.rooms.RoomContent;
import com.faust.lhitgame.game.world.manager.CollisionManager;

import java.util.Objects;

/**
 * Hurt spell instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class HurtSpellInstance extends GameInstance implements Interactable, Damager, Killable {

    private static final float PROJECTILE_SPEED = 70;

    private final Vector2 target; // Target x and y;

    private GameBehavior currentBehavior;

    public HurtSpellInstance(float x, float y, PlayerInstance playerInstance) {
        super(new ParticleEffectEntity("hurt_spell"));
        this.startX = x;
        this.startY = y;

        target = playerInstance.getBody().getPosition().cpy();
        currentBehavior = GameBehavior.WALK;
        ((ParticleEffectEntity) entity).getParticleEffect().start();;

    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        //Translate emitter
        ((ParticleEffectEntity) entity).getParticleEffect().setPosition(body.getPosition().x, body.getPosition().y);

        if (GameBehavior.WALK.equals(currentBehavior)) {
            Vector2 direction = new Vector2(target.x - body.getPosition().x, target.y - body.getPosition().y).nor();

            // Move towards target
            body.setLinearVelocity(PROJECTILE_SPEED * direction.x, PROJECTILE_SPEED * direction.y);

        } else {
            throw new GdxRuntimeException("Unexpected MeatInstance behaviour!");

        }
    }

    @Override
    public void createBody(World world, float x, float y) {

        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x, y);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4, 4);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 1;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;
        fixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);
        shape.dispose();

    }

    /**
     * Draw the Entity frames using Body position
     *
     * @param batch
     * @param stateTime
     */
    public void draw(final SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        batch.begin();
        ((ParticleEffectEntity) entity).getParticleEffect().draw(batch,Gdx.graphics.getDeltaTime());
        batch.end();
    }

    @Override
    public boolean isDisposable() {
        return isDead();
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        // Bounce player away
        playerInstance.hurt(this);
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        // Nothing to do here... yet
    }

    public double damageRoll() {
        return 2;
    }

    @Override
    public boolean isDying() {
        //is never dying...
        return false;
    }

    /**
     * @return true if outside of screen
     */
    @Override
    public boolean isDead() {
        final Vector2 position = body.getPosition();
        return position.x < 0 || position.x > LHITGame.GAME_WIDTH || position.y < 0 || position.y > LHITGame.GAME_HEIGHT;
    }
}
