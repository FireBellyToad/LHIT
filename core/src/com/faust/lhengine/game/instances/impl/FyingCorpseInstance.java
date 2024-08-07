package com.faust.lhengine.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhengine.game.gameentities.enums.ItemEnum;
import com.faust.lhengine.game.instances.ChaserInstance;
import com.faust.lhengine.game.instances.interfaces.Killable;
import com.faust.lhengine.game.rooms.RoomContent;
import com.faust.lhengine.game.world.interfaces.RayCaster;
import com.faust.lhengine.game.world.manager.CollisionManager;
import com.faust.lhengine.game.gameentities.AnimatedEntity;
import com.faust.lhengine.game.gameentities.enums.DirectionEnum;
import com.faust.lhengine.game.gameentities.enums.GameBehavior;
import com.faust.lhengine.game.gameentities.impl.FyingCorpseEntity;
import com.faust.lhengine.game.instances.interfaces.Damager;
import com.faust.lhengine.game.instances.interfaces.Hurtable;
import com.faust.lhengine.game.instances.GameInstance;
import com.faust.lhengine.game.instances.interfaces.Interactable;
import com.faust.lhengine.screens.impl.GameScreen;
import com.faust.lhengine.utils.LoggerUtils;

import java.util.Objects;

/**
 * Bounded enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FyingCorpseInstance extends ChaserInstance implements Interactable, Hurtable, Damager {

    private static final float SPEED = 38;
    private static final int LINE_OF_ATTACK = 15;
    private static final float CLAW_SENSOR_Y_OFFSET = 10;
    private static final int ATTACK_VALID_FRAME = 3; // Frame to activate attack sensor
    private static final long ATTACK_COOLDOWN_TIME = 200; // in millis

    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;
    private long startAttackCooldown = 0;

    //Body for spear attacks
    private Body downClawBody;
    private Body leftClawBody;
    private Body rightClawBody;
    private Body upClawBody;

    public FyingCorpseInstance(float x, float y, PlayerInstance target, AssetManager assetManager, RayCaster rayCaster) {
        super(new FyingCorpseEntity(assetManager),target,rayCaster);
        currentDirectionEnum = DirectionEnum.DOWN;
        this.startX = x;
        this.startY = y;
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        translateAccessoryBodies();

        if (GameBehavior.EVADE.equals(getCurrentBehavior()) || GameBehavior.HURT.equals(getCurrentBehavior()) || GameBehavior.DEAD.equals(getCurrentBehavior()))
            return;

        //Try to attack if not dead. If is attacking and is too far away, still needs to end the attack before following
        //the player
        if (!((Killable)target).isDead() && TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(ATTACK_COOLDOWN_TIME) && (GameBehavior.ATTACK.equals(getCurrentBehavior()) ||
                target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_ATTACK)) {

            //Start animation
            if (!GameBehavior.ATTACK.equals(getCurrentBehavior())) {
                attackDeltaTime = stateTime;
                changeCurrentBehavior(GameBehavior.ATTACK);
            }

            // Normal from bounded position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();
            currentDirectionEnum = extractDirectionFromNormal(direction);

            attackLogic(stateTime);
            body.setLinearVelocity(0, 0);

        } else if (TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(ATTACK_COOLDOWN_TIME) && target.getBody().getPosition().dst(getBody().getPosition()) > LINE_OF_ATTACK && (canSeeTarget() || isAggressive )) {

            deactivateAttackBodies();
            isAggressive = true;
            changeCurrentBehavior(GameBehavior.WALK);
            calculateNewGoal(roomContent.roomGraph);

            // Normal from Bounded position to target
            final Vector2 destination = getMovementDestination();
            Vector2 direction = new Vector2(destination.x - body.getPosition().x,
                    destination.y - body.getPosition().y).nor();

            currentDirectionEnum = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(SPEED * direction.x, SPEED * direction.y);
            deactivateAttackBodies();
        } else {
            changeCurrentBehavior(GameBehavior.IDLE);

            body.setLinearVelocity(0, 0);
        }
    }

    /**
     * Translate all accessory body
     */
    private void translateAccessoryBodies() {
        rightClawBody.setTransform(body.getPosition().x + 10, body.getPosition().y + CLAW_SENSOR_Y_OFFSET, 0);
        upClawBody.setTransform(body.getPosition().x, body.getPosition().y + 11 + CLAW_SENSOR_Y_OFFSET, 0);
        leftClawBody.setTransform(body.getPosition().x - 10, body.getPosition().y + CLAW_SENSOR_Y_OFFSET, 0);
        downClawBody.setTransform(body.getPosition().x, body.getPosition().y - 11 + CLAW_SENSOR_Y_OFFSET, 0);
        hitBox.setTransform(body.getPosition().x, body.getPosition().y + 8, 0);
    }

    /**
     * @return true if the damage is greater or equal than the resitance
     */
    @Override
    public boolean isDying() {
        return this.damage >= getResistance();
    }

    @Override
    public boolean isDead() {
        return GameBehavior.DEAD.equals(getCurrentBehavior());
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
        shape.setAsBox(4, 2);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;
        fixtureDef.filter.maskBits = CollisionManager.SOLID_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);
        shape.dispose();

        BodyDef rightClawDef = new BodyDef();
        rightClawDef.type = BodyDef.BodyType.KinematicBody;
        rightClawDef.fixedRotation = true;
        rightClawDef.position.set(x + 2, y);

        // Define shape
        PolygonShape rightClawShape = new PolygonShape();
        rightClawShape.setAsBox(4, 6);

        // Define Fixtures
        FixtureDef rightClawFixtureDef = new FixtureDef();
        rightClawFixtureDef.shape = rightClawShape;
        rightClawFixtureDef.density = 1;
        rightClawFixtureDef.friction = 1;
        rightClawFixtureDef.isSensor = true;
        rightClawFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        rightClawFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        rightClawBody = world.createBody(rightClawDef);
        rightClawBody.setUserData(this);
        rightClawBody.createFixture(rightClawFixtureDef);
        rightClawBody.setActive(false);
        rightClawShape.dispose();

        BodyDef upClawDef = new BodyDef();
        upClawDef.type = BodyDef.BodyType.KinematicBody;
        upClawDef.fixedRotation = true;
        upClawDef.position.set(x, y - 2);

        // Define shape
        PolygonShape upClawShape = new PolygonShape();
        upClawShape.setAsBox(6, 4);

        // Define Fixtures
        FixtureDef upClawFixtureDef = new FixtureDef();
        upClawFixtureDef.shape = upClawShape;
        upClawFixtureDef.density = 1;
        upClawFixtureDef.friction = 1;
        upClawFixtureDef.isSensor = true;
        upClawFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        upClawFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        upClawBody = world.createBody(upClawDef);
        upClawBody.setUserData(this);
        upClawBody.createFixture(upClawFixtureDef);
        upClawBody.setActive(false);
        upClawShape.dispose();

        BodyDef leftClawDef = new BodyDef();
        leftClawDef.type = BodyDef.BodyType.KinematicBody;
        leftClawDef.fixedRotation = true;
        leftClawDef.position.set(x - 2, y);

        // Define shape
        PolygonShape leftClawShape = new PolygonShape();
        leftClawShape.setAsBox(4, 6);

        // Define Fixtures
        FixtureDef leftClawFixtureDef = new FixtureDef();
        leftClawFixtureDef.shape = leftClawShape;
        leftClawFixtureDef.density = 1;
        leftClawFixtureDef.friction = 1;
        leftClawFixtureDef.isSensor = true;
        leftClawFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        leftClawFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        leftClawBody = world.createBody(leftClawDef);
        leftClawBody.setUserData(this);
        leftClawBody.createFixture(leftClawFixtureDef);
        leftClawBody.setActive(false);
        leftClawShape.dispose();

        BodyDef downClawDef = new BodyDef();
        downClawDef.type = BodyDef.BodyType.KinematicBody;
        downClawDef.fixedRotation = true;
        downClawDef.position.set(x, y + 2);

        // Define shape
        PolygonShape downClawShape = new PolygonShape();
        downClawShape.setAsBox(6, 4);

        // Define Fixtures
        FixtureDef downClawFixtureDef = new FixtureDef();
        downClawFixtureDef.shape = downClawShape;
        downClawFixtureDef.density = 1;
        downClawFixtureDef.friction = 1;
        downClawFixtureDef.isSensor = true;
        downClawFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        downClawFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        downClawBody = world.createBody(downClawDef);
        downClawBody.setUserData(this);
        downClawBody.createFixture(downClawFixtureDef);
        downClawBody.setActive(false);
        downClawShape.dispose();

        // Hitbox definition
        BodyDef hitBoxDef = new BodyDef();
        hitBoxDef.type = BodyDef.BodyType.DynamicBody;
        hitBoxDef.fixedRotation = true;
        hitBoxDef.position.set(x, y);

        // Define shape
        PolygonShape hitBoxShape = new PolygonShape();
        hitBoxShape.setAsBox(4, 12);

        // Define Fixture
        FixtureDef hitBoxFixtureDef = new FixtureDef();
        hitBoxFixtureDef.shape = hitBoxShape;
        hitBoxFixtureDef.density = 0;
        hitBoxFixtureDef.friction = 0;
        hitBoxFixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;
        hitBoxFixtureDef.filter.maskBits = CollisionManager.WEAPON_GROUP;

        // Associate body to world
        hitBox = world.createBody(hitBoxDef);
        hitBox.setUserData(this);
        hitBox.createFixture(hitBoxFixtureDef);
        hitBoxShape.dispose();
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
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(getCurrentBehavior(), currentDirectionEnum, mapStateTimeFromBehaviour(stateTime), !GameBehavior.ATTACK.equals(getCurrentBehavior()));

        Vector2 drawPosition = adjustPosition();
        //Draw shadow
        batch.draw(((FyingCorpseEntity) entity).getShadowTexture(), drawPosition.x - POSITION_OFFSET, drawPosition.y - 2 - POSITION_Y_OFFSET);

        //Draw Bounded

        // If not hurt or the flickering POI must be shown, draw the texture
        if (!mustFlicker || !GameBehavior.HURT.equals(getCurrentBehavior())) {
            batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET);
        }

        // Every 1/8 seconds alternate between showing and hiding the texture to achieve flickering effect
        if (GameBehavior.HURT.equals(getCurrentBehavior()) && TimeUtils.timeSinceNanos(startToFlickTime) > GameScreen.FLICKER_DURATION_IN_NANO / 6) {
            mustFlicker = !mustFlicker;

            // restart flickering timer
            startToFlickTime = TimeUtils.nanoTime();
        }
        batch.end();
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {

    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        // End leech and cancel timer if present
    }

    /**
     * Method for hurting the Bounded
     *
     * @param attacker
     */
    @Override
    public void hurt(GameInstance attacker) {

        //40% chance of evading attack
        final boolean canEvade = (MathUtils.random(1, 100)) >= 60;
        if (isDying()) {
            ((FyingCorpseEntity) entity).playDeathCry();
            body.setLinearVelocity(0, 0);
            changeCurrentBehavior(GameBehavior.DEAD);
        } else if (!canEvade && !GameBehavior.HURT.equals(getCurrentBehavior())) {
            ((FyingCorpseEntity) entity).playHurtCry();

            // Hurt by player
            double amount = ((Damager) attacker).damageRoll();
            //If Undead or Otherworldly, halve normal lance damage
            if (((PlayerInstance) attacker).getItemQuantityFound(ItemEnum.HOLY_LANCE) < 2) {
                amount = Math.floor(amount / 2);
            }

            this.damage += Math.min(getResistance(), amount);
            changeCurrentBehavior(GameBehavior.HURT);
            Gdx.app.log(LoggerUtils.DEBUG_TAG, "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
            postHurtLogic(attacker);
        } else if (canEvade && !GameBehavior.EVADE.equals(getCurrentBehavior())) {
            ((FyingCorpseEntity) entity).playEvadeSwift();
            //Just evade
            changeCurrentBehavior(GameBehavior.EVADE);
            Gdx.app.log(LoggerUtils.DEBUG_TAG, "Instance EVADED!");
            postHurtLogic(attacker);
        }

    }

    @Override
    public void postHurtLogic(GameInstance attacker) {

        // is pushed away while flickering
        Vector2 direction = new Vector2(attacker.getBody().getPosition().x - body.getPosition().x,
                attacker.getBody().getPosition().y - body.getPosition().y).nor();

        float modifier = 4f;
        //If evading, the leap is more subtle and perpendicular in a random direction
        if (GameBehavior.EVADE.equals(getCurrentBehavior())) {
            modifier = 1.5f;
            direction = direction.rotate90(MathUtils.randomBoolean() ? 0 :1 );
        }
        body.setLinearVelocity(SPEED * modifier * -direction.x, SPEED * modifier * -direction.y);
        // Do nothing for half second
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                changeCurrentBehavior(GameBehavior.IDLE);
            }
        }, 0.25f);
    }


    @Override
    public int getResistance() {
        return 6;
    }

    public double damageRoll() {
        return 2;
    }

    /**
     * Handle the attack logic, activating and deactivating attack collision bodies
     *
     * @param stateTime
     */
    private void attackLogic(float stateTime) {

        int currentFrame = ((AnimatedEntity) entity).getFrameIndex(getCurrentBehavior(), currentDirectionEnum, mapStateTimeFromBehaviour(stateTime));

        //Activate weapon sensor on frame
        if (currentFrame == ATTACK_VALID_FRAME) {
            startAttackCooldown = TimeUtils.nanoTime();
            switch (currentDirectionEnum) {
                case UP: {
                    upClawBody.setActive(true);
                    break;
                }
                case DOWN: {
                    downClawBody.setActive(true);
                    break;
                }
                case LEFT: {
                    leftClawBody.setActive(true);
                    break;
                }
                case RIGHT: {
                    rightClawBody.setActive(true);
                    break;
                }
            }
        } else {
            deactivateAttackBodies();
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        rightClawBody.getFixtureList().forEach(f ->
                rightClawBody.destroyFixture(f));
        leftClawBody.getFixtureList().forEach(f ->
                leftClawBody.destroyFixture(f));
        upClawBody.getFixtureList().forEach(f ->
                upClawBody.destroyFixture(f));
        downClawBody.getFixtureList().forEach(f ->
                downClawBody.destroyFixture(f));
    }

    @Override
    public boolean isDisposable() {
        return isDead();
    }

    private float mapStateTimeFromBehaviour(float stateTime) {

        if (getCurrentBehavior() == GameBehavior.ATTACK) {
            return (stateTime - attackDeltaTime);
        }
        return stateTime;
    }

    /**
     * Deactivate all attacker bodies
     */
    private void deactivateAttackBodies() {
        rightClawBody.setActive(false);
        upClawBody.setActive(false);
        leftClawBody.setActive(false);
        downClawBody.setActive(false);
    }

    @Override
    protected float getLineOfSight() {
        return 70f;
    }
}
