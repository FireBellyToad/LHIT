package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.enums.DirectionEnum;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.gameentities.impl.DiaconusEntity;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.PathfinderInstance;
import com.faust.lhitgame.game.instances.interfaces.Damager;
import com.faust.lhitgame.game.instances.interfaces.Hurtable;
import com.faust.lhitgame.game.instances.interfaces.Interactable;
import com.faust.lhitgame.game.rooms.RoomContent;
import com.faust.lhitgame.game.world.interfaces.RayCaster;
import com.faust.lhitgame.game.world.manager.CollisionManager;
import com.faust.lhitgame.screens.GameScreen;

import java.util.Objects;

/**
 * Diaconus secret boss instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class DiaconusInstance extends PathfinderInstance implements Interactable, Hurtable, Damager {

    private static final float DIACONUS_SPEED = 35;
    private static final int LINE_OF_ATTACK = 15;
    private static final float SPATHA_SENSOR_Y_OFFSET = 10;
    private static final int ATTACK_VALID_FRAME = 3; // Frame to activate attack sensor
    private static final long ATTACK_COOLDOWN_TIME = 750; // in millis

    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;
    private boolean isAggressive = false;
    private boolean isSubmerged = false;

    private long startAttackCooldown = 0;

    //Body for spear attacks
    private Body downSpathaBody;
    private Body leftSpathaBody;
    private Body rightSpathaBody;
    private Body upSpathaBody;

    private final ParticleEffect waterWalkEffect;

    public DiaconusInstance(float x, float y, PlayerInstance target, AssetManager assetManager, RayCaster rayCaster) {
        super(new DiaconusEntity(assetManager), target, rayCaster);
        currentDirectionEnum = DirectionEnum.DOWN;
        this.startX = x;
        this.startY = y;

        // Init waterwalk effect
        waterWalkEffect = new ParticleEffect();
        // First is particle configuration, second is particle sprite path (file is embeeded in configuration)
        waterWalkEffect.load(Gdx.files.internal("particles/waterwalk"), Gdx.files.internal("sprites/"));
        waterWalkEffect.start();
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        translateAccessoryBodies();
        waterWalkEffect.getEmitters().first().setPosition(body.getPosition().x, body.getPosition().y);

        if (GameBehavior.EVADE.equals(currentBehavior) || GameBehavior.HURT.equals(currentBehavior) || GameBehavior.DEAD.equals(currentBehavior))
            return;

        if (TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(ATTACK_COOLDOWN_TIME) &&
                target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_ATTACK) {

            //Start animation
            if (!GameBehavior.ATTACK.equals(currentBehavior)) {
                attackDeltaTime = stateTime;
                currentBehavior = GameBehavior.ATTACK;
            }

            // Normal from Diaconus position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();
            currentDirectionEnum = extractDirectionFromNormal(direction);

            attackLogic(stateTime);
            body.setLinearVelocity(0, 0);

        } else if (target.getBody().getPosition().dst(getBody().getPosition()) > LINE_OF_ATTACK && (canSeePlayer() || isAggressive)) {

            deactivateAttackBodies();
            isAggressive = true;
            currentBehavior = GameBehavior.WALK;
            calculateNewGoal(roomContent.roomGraph);

            // Normal from Diaconus position to target
            final Vector2 destination = getMovementDestination();
            Vector2 direction = new Vector2(destination.x - body.getPosition().x,
                    destination.y - body.getPosition().y).nor();

            currentDirectionEnum = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(DIACONUS_SPEED * direction.x, DIACONUS_SPEED * direction.y);
            deactivateAttackBodies();
        } else {
            currentBehavior = GameBehavior.IDLE;

            body.setLinearVelocity(0, 0);
        }

    }

    /**
     * Translate all accessory body
     */
    private void translateAccessoryBodies() {
        rightSpathaBody.setTransform(body.getPosition().x + 10, body.getPosition().y + SPATHA_SENSOR_Y_OFFSET, 0);
        upSpathaBody.setTransform(body.getPosition().x, body.getPosition().y + 11 + SPATHA_SENSOR_Y_OFFSET, 0);
        leftSpathaBody.setTransform(body.getPosition().x - 10, body.getPosition().y + SPATHA_SENSOR_Y_OFFSET, 0);
        downSpathaBody.setTransform(body.getPosition().x, body.getPosition().y - 11 + SPATHA_SENSOR_Y_OFFSET, 0);
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
        return GameBehavior.DEAD.equals(currentBehavior);
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

        BodyDef rightSpathaDef = new BodyDef();
        rightSpathaDef.type = BodyDef.BodyType.KinematicBody;
        rightSpathaDef.fixedRotation = true;
        rightSpathaDef.position.set(x + 2, y);

        // Define shape
        PolygonShape rightSpathaShape = new PolygonShape();
        rightSpathaShape.setAsBox(4, 6);

        // Define Fixtures
        FixtureDef rightSpathaFixtureDef = new FixtureDef();
        rightSpathaFixtureDef.shape = rightSpathaShape;
        rightSpathaFixtureDef.density = 1;
        rightSpathaFixtureDef.friction = 1;
        rightSpathaFixtureDef.isSensor = true;
        rightSpathaFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        rightSpathaFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        rightSpathaBody = world.createBody(rightSpathaDef);
        rightSpathaBody.setUserData(this);
        rightSpathaBody.createFixture(rightSpathaFixtureDef);
        rightSpathaBody.setActive(false);
        rightSpathaShape.dispose();

        BodyDef upSpathaDef = new BodyDef();
        upSpathaDef.type = BodyDef.BodyType.KinematicBody;
        upSpathaDef.fixedRotation = true;
        upSpathaDef.position.set(x, y - 2);

        // Define shape
        PolygonShape upSpathaShape = new PolygonShape();
        upSpathaShape.setAsBox(6, 4);

        // Define Fixtures
        FixtureDef upSpathaFixtureDef = new FixtureDef();
        upSpathaFixtureDef.shape = upSpathaShape;
        upSpathaFixtureDef.density = 1;
        upSpathaFixtureDef.friction = 1;
        upSpathaFixtureDef.isSensor = true;
        upSpathaFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        upSpathaFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        upSpathaBody = world.createBody(upSpathaDef);
        upSpathaBody.setUserData(this);
        upSpathaBody.createFixture(upSpathaFixtureDef);
        upSpathaBody.setActive(false);
        upSpathaShape.dispose();

        BodyDef leftSpathaDef = new BodyDef();
        leftSpathaDef.type = BodyDef.BodyType.KinematicBody;
        leftSpathaDef.fixedRotation = true;
        leftSpathaDef.position.set(x - 2, y);

        // Define shape
        PolygonShape leftSpathaShape = new PolygonShape();
        leftSpathaShape.setAsBox(4, 6);

        // Define Fixtures
        FixtureDef leftSpathaFixtureDef = new FixtureDef();
        leftSpathaFixtureDef.shape = leftSpathaShape;
        leftSpathaFixtureDef.density = 1;
        leftSpathaFixtureDef.friction = 1;
        leftSpathaFixtureDef.isSensor = true;
        leftSpathaFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        leftSpathaFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        leftSpathaBody = world.createBody(leftSpathaDef);
        leftSpathaBody.setUserData(this);
        leftSpathaBody.createFixture(leftSpathaFixtureDef);
        leftSpathaBody.setActive(false);
        leftSpathaShape.dispose();

        BodyDef downSpathaDef = new BodyDef();
        downSpathaDef.type = BodyDef.BodyType.KinematicBody;
        downSpathaDef.fixedRotation = true;
        downSpathaDef.position.set(x, y + 2);

        // Define shape
        PolygonShape downSpathaShape = new PolygonShape();
        downSpathaShape.setAsBox(6, 4);

        // Define Fixtures
        FixtureDef downSpathaFixtureDef = new FixtureDef();
        downSpathaFixtureDef.shape = downSpathaShape;
        downSpathaFixtureDef.density = 1;
        downSpathaFixtureDef.friction = 1;
        downSpathaFixtureDef.isSensor = true;
        downSpathaFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        downSpathaFixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        downSpathaBody = world.createBody(downSpathaDef);
        downSpathaBody.setUserData(this);
        downSpathaBody.createFixture(downSpathaFixtureDef);
        downSpathaBody.setActive(false);
        downSpathaShape.dispose();

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
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime), !GameBehavior.ATTACK.equals(currentBehavior));

        int yOffset = 0;

        //Draw watersteps if submerged
        if (isSubmerged) {
            waterWalkEffect.update(Gdx.graphics.getDeltaTime());
            waterWalkEffect.draw(batch);
            yOffset += 2;
            // Do not loop if is not doing anything
            if (waterWalkEffect.isComplete() && GameBehavior.WALK.equals(currentBehavior)) {
                waterWalkEffect.reset();
            }
        } else {
            waterWalkEffect.reset();
        }

        //Draw shadow
        batch.draw(((DiaconusEntity) entity).getShadowTexture(), body.getPosition().x - POSITION_OFFSET, body.getPosition().y - yOffset - 2 - POSITION_Y_OFFSET);

        //Draw Diaconus
        // If not hurt or the flickering POI must be shown, draw the texture
        if (!mustFlicker || !GameBehavior.HURT.equals(currentBehavior)) {
            batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);
        }

        // Every 1/8 seconds alternate between showing and hiding the texture to achieve flickering effect
        if (GameBehavior.HURT.equals(currentBehavior) && TimeUtils.timeSinceNanos(startToFlickTime) > GameScreen.FLICKER_DURATION_IN_NANO / 6) {
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
     * Method for hurting the Diaconus
     *
     * @param attacker
     */
    @Override
    public void hurt(GameInstance attacker) {

        //50% chance of evading attack
        final boolean canEvade = (MathUtils.random(1, 100)) >= 50;
        if (isDying()) {
            ((DiaconusEntity) entity).playDeathCry();
            body.setLinearVelocity(0, 0);
            currentBehavior = GameBehavior.DEAD;
        } else if (!canEvade && !GameBehavior.HURT.equals(currentBehavior)) {
            ((DiaconusEntity) entity).playHurtCry();

            // Hurt by player
            double amount = ((Damager) attacker).damageRoll();
            //Diaconus halves normal lance damage
            if (((PlayerInstance) attacker).getHolyLancePieces() < 2) {
                amount = Math.floor(amount / 2);
            }

            this.damage += Math.min(getResistance(), amount);
            currentBehavior = GameBehavior.HURT;
            Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
            postHurtLogic(attacker);
        } else if (canEvade && !GameBehavior.EVADE.equals(currentBehavior)) {
            ((DiaconusEntity) entity).playEvadeSwift();
            //Just evade
            currentBehavior = GameBehavior.EVADE;
            Gdx.app.log("DEBUG", "Instance EVADED!");
            postHurtLogic(attacker);
        }

    }

    @Override
    public void postHurtLogic(GameInstance attacker) {

        // is pushed away while flickering
        Vector2 direction = new Vector2(attacker.getBody().getPosition().x - body.getPosition().x,
                attacker.getBody().getPosition().y - body.getPosition().y).nor();

        float modifier = 4f;
        //If evading, the leap is more subtle and perpendicular
        if (GameBehavior.EVADE.equals(currentBehavior)) {
            modifier = 1.5f;
            direction.x = (float) Math.cos(direction.x);
            direction.y = (float) Math.cos(direction.y);
        }
        body.setLinearVelocity(DIACONUS_SPEED * modifier * -direction.x, DIACONUS_SPEED * modifier * -direction.y);
        // Do nothing for half second
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                currentBehavior = GameBehavior.IDLE;
            }
        }, 0.25f);
    }


    @Override
    public int getResistance() {
        return 9;
    }

    public double damageRoll() {
        return 3;
    }

    /**
     * Handle the attack logic, activating and deactivating attack collision bodies
     *
     * @param stateTime
     */
    private void attackLogic(float stateTime) {

        int currentFrame = ((AnimatedEntity) entity).getFrameIndex(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime));

        //Activate weapon sensor on frame
        if (currentFrame == ATTACK_VALID_FRAME) {
            startAttackCooldown = TimeUtils.nanoTime();
            switch (currentDirectionEnum) {
                case UP: {
                    upSpathaBody.setActive(true);
                    break;
                }
                case DOWN: {
                    downSpathaBody.setActive(true);
                    break;
                }
                case LEFT: {
                    leftSpathaBody.setActive(true);
                    break;
                }
                case RIGHT: {
                    rightSpathaBody.setActive(true);
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
        rightSpathaBody.getFixtureList().forEach(f ->
                rightSpathaBody.destroyFixture(f));
        leftSpathaBody.getFixtureList().forEach(f ->
                leftSpathaBody.destroyFixture(f));
        upSpathaBody.getFixtureList().forEach(f ->
                upSpathaBody.destroyFixture(f));
        downSpathaBody.getFixtureList().forEach(f ->
                downSpathaBody.destroyFixture(f));
        waterWalkEffect.dispose();
    }

    @Override
    public boolean isDisposable() {
        return isDead();
    }

    private float mapStateTimeFromBehaviour(float stateTime) {

        switch (currentBehavior) {
            case ATTACK: {
                return (stateTime - attackDeltaTime);
            }
            default: {
                return stateTime;
            }
        }
    }

    /**
     * Deactivate all attacker bodies
     */
    private void deactivateAttackBodies() {
        rightSpathaBody.setActive(false);
        upSpathaBody.setActive(false);
        leftSpathaBody.setActive(false);
        downSpathaBody.setActive(false);
    }

    @Override
    protected float getLineOfSight() {
        return 90f;
    }

    public void setSubmerged(boolean submerged) {
        isSubmerged = submerged;
    }
}