package faust.lhipgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.SpitterEntity;
import faust.lhipgame.game.gameentities.interfaces.Damager;
import faust.lhipgame.game.gameentities.interfaces.Hurtable;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.Spawner;
import faust.lhipgame.game.instances.interfaces.Interactable;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.game.world.manager.CollisionManager;
import faust.lhipgame.screens.GameScreen;

import java.util.Objects;

/**
 * Spitter enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SpitterInstance extends AnimatedInstance implements Interactable, Hurtable, Damager {

    private static final int ATTACK_VALID_FRAME = 6; // Frame to activate attack sensor
    private static final long SPITTING_FREQUENCY_IN_MILLIS = 1750;

    private final TextBoxManager textBoxManager;
    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;

    private final Spawner spawner;

    private long startAttackCooldown = 0;

    private boolean isDead = false;
    private boolean canAttack = false;


    public SpitterInstance(float x, float y, AssetManager assetManager, TextBoxManager textBoxManager, Spawner spawner) {
        super(new SpitterEntity(assetManager));
        currentDirection = Direction.DOWN;
        this.startX = x;
        this.startY = y;
        this.textBoxManager = textBoxManager;
        this.spawner = spawner;
    }

    @Override
    public void doLogic(float stateTime) {

        switch (currentBehavior) {
            case ATTACK: {

                attackLogic(stateTime);
                break;
            }
            case IDLE: {
                // Every six seconds spits meat
                if (TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(SPITTING_FREQUENCY_IN_MILLIS)) {
                    readyToAttack(stateTime);
                }

                break;
            }
            case DEAD: {
                // Stay dead
                break;
            }
            case HURT: {
                // Spit asap
                startAttackCooldown = 0;
                break;
            }
            default: {
                throw new GdxRuntimeException("Unexpected SpitterInstance behaviour!");
            }
        }

    }

    private void readyToAttack(float stateTime) {
        attackDeltaTime = stateTime;
        currentBehavior = GameBehavior.ATTACK;
        canAttack = true;
        startAttackCooldown = TimeUtils.nanoTime();
    }

    @Override
    public void postHurtLogic(GameInstance attacker) {

        currentBehavior = GameBehavior.HURT;
        // Do nothing for half second
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                currentBehavior = GameBehavior.IDLE;
            }
        }, 0.25f);
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
        return isDead;
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
        shape.setAsBox(8, 2);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;
        fixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

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
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, mapStateTimeFromBehaviour(stateTime), true);

        //Draw Spitter
        // If not hurt or the flickering Spitter must be shown, draw the texture
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
        // End leech and cancel timer if present
    }

    /**
     * Method for hurting the Spitter
     *
     * @param attacker
     */
    @Override
    public void hurt(GameInstance attacker) {
        if (isDying()) {
            ((SpitterEntity) entity).playDeathCry();
            isDead = true;
            currentBehavior = GameBehavior.DEAD;
        } else if (!GameBehavior.HURT.equals(currentBehavior)) {
            ((SpitterEntity) entity).playHurtCry();

            //If Undead or Otherworldly, halve normal lance damage
            //Usually should never happen for spitter
            if (((PlayerInstance) attacker).getHolyLancePieces() < 2) {
                textBoxManager.addNewTextBox("warn.hive.damage");
                return;
            }

            // Hurt by player
            this.damage += ((Damager) attacker).damageRoll();
            Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
            postHurtLogic(attacker);
        }
    }

    @Override
    public int getResistance() {
        return 15;
    }

    public double damageRoll() {
        return 0; // harmless! just bounce player
    }

    /**
     * Handle the attack logic, activating and deactivating attack collision bodies
     *
     * @param stateTime
     */
    private void attackLogic(float stateTime) {

        int currentFrame = ((AnimatedEntity) entity).getFrameIndex(currentBehavior, currentDirection, mapStateTimeFromBehaviour(stateTime));

        //Activate weapon sensor on frame
        if (currentFrame == ATTACK_VALID_FRAME && canAttack) {
            ((SpitterEntity) entity).playSpitSound();
            spawner.spawnInstance(MeatInstance.class, this.startX, this.startY);
            canAttack = false;
        }
        // Resetting Behaviour on animation end
        if (((AnimatedEntity) entity).isAnimationFinished(currentBehavior, currentDirection, mapStateTimeFromBehaviour(stateTime))) {
            currentBehavior = GameBehavior.IDLE;
        }
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {

        switch (currentBehavior) {
            case ATTACK: {
                return (stateTime - attackDeltaTime);
            }
            default: {
                return stateTime;
            }
        }
    }

}
