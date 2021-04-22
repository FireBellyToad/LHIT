package faust.lhipgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.Fightable;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.BoundedEntity;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.Interactable;
import faust.lhipgame.game.world.manager.CollisionManager;
import faust.lhipgame.screens.GameScreen;

import java.util.Objects;

/**
 * Bounded enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class BoundedInstance extends AnimatedInstance implements Interactable, Fightable {

    private static final float BOUNDED_SPEED = 35;
    private static final int LINE_OF_ATTACK = 15;
    private static final int LINE_OF_SIGHT = 70;
    private static final float CLAW_SENSOR_Y_OFFSET = 10;
    private static final int ATTACK_VALID_FRAME = 3; // Frame to activate attack sensor
    private static final float ATTACK_COOLDOWN_TIME = 2;

    //Body for spear attacks
    private Body downClawBody;
    private Body leftClawBody;
    private Body rightClawBody;
    private Body upClawBody;

    private final PlayerInstance target;

    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;
    private boolean attackCooldown = true;
    private Timer.Task attackCooldownTimer;

    public BoundedInstance(float x, float y, PlayerInstance target, AssetManager assetManager) {
        super(new BoundedEntity(assetManager));
        currentDirection = Direction.DOWN;
        this.startX = x;
        this.startY = y;
        this.target = target;
    }

    @Override
    public void doLogic(float stateTime) {

        translateAccessoryBodies();

        if (GameBehavior.EVADE.equals(currentBehavior) || GameBehavior.HURT.equals(currentBehavior) || GameBehavior.DEAD.equals(currentBehavior))
            return;

        if (attackCooldown && target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_ATTACK) {

            //Start animation
            if(!GameBehavior.ATTACK.equals(currentBehavior)){
                attackDeltaTime = stateTime;
                currentBehavior = GameBehavior.ATTACK;
            }

            // Normal from bounded position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();
            currentDirection = extractDirectionFromNormal(direction);

            attackLogic(stateTime);
            body.setLinearVelocity(0, 0);

        } else if (target.getBody().getPosition().dst(getBody().getPosition()) > LINE_OF_ATTACK &&
                target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_SIGHT) {
            currentBehavior = GameBehavior.WALK;
            // Normal from strix position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();

            currentDirection = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(BOUNDED_SPEED * direction.x, BOUNDED_SPEED * direction.y);
            rightClawBody.setActive(false);
            upClawBody.setActive(false);
            leftClawBody.setActive(false);
            downClawBody.setActive(false);
        } else {
            currentBehavior = GameBehavior.IDLE;

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

        BodyDef rightClawDef = new BodyDef();
        rightClawDef.type = BodyDef.BodyType.KinematicBody;
        rightClawDef.fixedRotation = true;
        rightClawDef.position.set(x+2, y);

        // Define shape
        PolygonShape rightClawShape = new PolygonShape();
        rightClawShape.setAsBox(3, 6);

        // Define Fixtures
        FixtureDef rightClawFixtureDef = new FixtureDef();
        rightClawFixtureDef.shape = shape;
        rightClawFixtureDef.density = 1;
        rightClawFixtureDef.friction = 1;
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
        upClawDef.position.set(x, y-2);

        // Define shape
        PolygonShape upClawShape = new PolygonShape();
        upClawShape.setAsBox(6, 3);

        // Define Fixtures
        FixtureDef upClawFixtureDef = new FixtureDef();
        upClawFixtureDef.shape = shape;
        upClawFixtureDef.density = 1;
        upClawFixtureDef.friction = 1;
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
        leftClawDef.position.set(x-2, y);

        // Define shape
        PolygonShape leftClawShape = new PolygonShape();
        leftClawShape.setAsBox(3, 6);

        // Define Fixtures
        FixtureDef leftClawFixtureDef = new FixtureDef();
        leftClawFixtureDef.shape = shape;
        leftClawFixtureDef.density = 1;
        leftClawFixtureDef.friction = 1;
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
        downClawDef.position.set(x, y+2);

        // Define shape
        PolygonShape downClawShape = new PolygonShape();
        downClawShape.setAsBox(6, 3);

        // Define Fixtures
        FixtureDef downClawFixtureDef = new FixtureDef();
        downClawFixtureDef.shape = shape;
        downClawFixtureDef.density = 1;
        downClawFixtureDef.friction = 1;
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
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, currentDirection,  mapStateTimeFromBehaviour(stateTime), !GameBehavior.ATTACK.equals(currentBehavior));

        //Draw shadow
        batch.draw(((BoundedEntity) entity).getShadowTexture(), body.getPosition().x - POSITION_OFFSET, body.getPosition().y - 2 - POSITION_Y_OFFSET);

        //Draw Bounded

        // If not hurt or the flickering POI must be shown, draw the texture
        if (!mustFlicker || !GameBehavior.HURT.equals(currentBehavior)) {
            batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);
        }

        // Every 1/8 seconds alternate between showing and hiding the texture to achieve flickering effect
        if (GameBehavior.HURT.equals(currentBehavior) && TimeUtils.timeSinceNanos(startTime) > GameScreen.FLICKER_DURATION_IN_NANO / 6) {
            mustFlicker = !mustFlicker;

            // restart flickering timer
            startTime = TimeUtils.nanoTime();
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

        //30% chance of evading attack
        final boolean canEvade = (MathUtils.random(1,100)) >= 70;
        if (isDying()) {
            ((BoundedEntity) entity).playDeathCry();
            body.setLinearVelocity(0, 0);
            currentBehavior = GameBehavior.DEAD;
        } else if (!canEvade && !GameBehavior.HURT.equals(currentBehavior)) {
            ((BoundedEntity) entity).playHurtCry();

            // Hurt by player
            double amount = ((Fightable)attacker).damageRoll();
            //If Undead or Otherworldly, halve normal lance damage
            if(((PlayerInstance) attacker).getHolyLancePieces() < 2){
                amount =  Math.floor(amount / 2);
            }

            this.damage += Math.min(getResistance(), amount);
            currentBehavior = GameBehavior.HURT;
            Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
            postHurtLogic(attacker);
        } else if (canEvade && !GameBehavior.EVADE.equals(currentBehavior)) {
            ((BoundedEntity) entity).playEvadeSwift();
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

        //If evading, the leap is more subtle
        final int modifier = GameBehavior.HURT.equals(currentBehavior) ? 4: 1;
        body.setLinearVelocity(BOUNDED_SPEED * modifier * -direction.x, BOUNDED_SPEED * modifier * -direction.y);
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

        int currentFrame = ((AnimatedEntity) entity).getFrameIndex(currentBehavior, currentDirection,  mapStateTimeFromBehaviour(stateTime));

        //Activate weapon sensor on frame
        if (currentFrame == ATTACK_VALID_FRAME) {
            switch (currentDirection) {
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
            rightClawBody.setActive(false);
            upClawBody.setActive(false);
            leftClawBody.setActive(false);
            downClawBody.setActive(false);
        }

        // Resetting Behaviour on animation end
        if (((AnimatedEntity) entity).isAnimationFinished(currentBehavior, currentDirection, mapStateTimeFromBehaviour(stateTime))) {
            attackCooldown = false;

            if(Objects.isNull(attackCooldownTimer)) {
                attackCooldownTimer = Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        attackCooldown = true;
                        attackCooldownTimer = null;
                    }
                }, ATTACK_COOLDOWN_TIME);
            }
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
    protected float mapStateTimeFromBehaviour(float stateTime) {

        switch (currentBehavior) {
            case ATTACK: {
                return (stateTime - attackDeltaTime);
            }
        }
        return stateTime;
    }

}
