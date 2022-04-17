package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.enums.DirectionEnum;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.gameentities.enums.ItemEnum;
import com.faust.lhitgame.game.gameentities.impl.DiaconusEntity;
import com.faust.lhitgame.game.gameentities.impl.PlayerEntity;
import com.faust.lhitgame.game.instances.DistancerInstance;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.Spawner;
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
public class DiaconusInstance extends DistancerInstance implements Interactable, Hurtable, Damager {

    private static final float DIACONUS_SPEED = 40;
    private static final int LINE_OF_ATTACK = 50;
    private static final int ATTACK_VALID_FRAME = 3; // Frame to activate attack sensor
    private static final long ATTACK_COOLDOWN_TIME = 750; // in millis
    private static final long ATTACK_COUNTER_LIMIT = 6; // in millis

    private final Spawner spawner;

    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;
    private boolean isSubmerged = false;

    private long startAttackCooldown = 0;

    public DiaconusInstance(float x, float y, PlayerInstance target, AssetManager assetManager, RayCaster rayCaster, Spawner spawner) {
        super(new DiaconusEntity(assetManager), target);
        this.spawner = spawner;
        currentDirectionEnum = DirectionEnum.DOWN;
        this.startX = x;
        this.startY = y;
        ((DiaconusEntity) entity).getWaterWalkEffect().start();
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        //Move hitbox with main body
        hitBox.setTransform(body.getPosition().x, body.getPosition().y + 8, 0);

        //Move emitter
        ((DiaconusEntity) entity).getWaterWalkEffect().setPosition(body.getPosition().x, body.getPosition().y);

        if (GameBehavior.EVADE.equals(currentBehavior) || GameBehavior.HURT.equals(currentBehavior) || GameBehavior.DEAD.equals(currentBehavior))
            return;

        if (TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(ATTACK_COOLDOWN_TIME) &&
                target.getBody().getPosition().dst(getBody().getPosition()) > LINE_OF_ATTACK) {

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

        } else if (target.getBody().getPosition().dst(getBody().getPosition()) < LINE_OF_ATTACK) {
            //FIXME add check that if still attacking keeps attacking
            currentBehavior = GameBehavior.WALK;
            calculateNewGoal(roomContent.roomGraph);

            // Normal from Diaconus position to target
            final Vector2 destination = getMovementDestination();
            Vector2 direction = new Vector2(destination.x - body.getPosition().x,
                    destination.y - body.getPosition().y).nor();

            currentDirectionEnum = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(DIACONUS_SPEED * direction.x, DIACONUS_SPEED * direction.y);
        } else {
            currentBehavior = GameBehavior.IDLE;

            body.setLinearVelocity(0, 0);
        }

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
        final ParticleEffect waterWalkEffect = ((DiaconusEntity) entity).getWaterWalkEffect();

        //Draw watersteps if submerged
        if (isSubmerged) {
            waterWalkEffect.draw(batch,Gdx.graphics.getDeltaTime());
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
            ((PlayerInstance) attacker).setHasKilledSecretBoss(true);
        } else if (!canEvade && !GameBehavior.HURT.equals(currentBehavior)) {
            ((DiaconusEntity) entity).playHurtCry();

            // Hurt by player
            double amount = ((Damager) attacker).damageRoll();
            //Diaconus halves normal lance damage
            if (((PlayerInstance) attacker).getItemQuantityFound(ItemEnum.HOLY_LANCE) < 2) {
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
            modifier = 2f;
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
        //FIXME set right time
        if (currentFrame == ATTACK_VALID_FRAME) {
            startAttackCooldown = TimeUtils.nanoTime();

            spawner.spawnInstance(ConfusionSpellInstance.class,
                    this.body.getPosition().x,
                    this.body.getPosition().y, null);

        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean isDisposable() {
        return isDead();
    }

    private float mapStateTimeFromBehaviour(float stateTime) {

        if (currentBehavior == GameBehavior.ATTACK) {
            return (stateTime - attackDeltaTime);
        }
        return stateTime;
    }

    public void setSubmerged(boolean submerged) {
        isSubmerged = submerged;
    }
}
