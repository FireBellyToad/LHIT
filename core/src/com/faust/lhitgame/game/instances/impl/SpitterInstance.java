package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhitgame.game.instances.AnimatedInstance;
import com.faust.lhitgame.game.rooms.RoomContent;
import com.faust.lhitgame.game.world.manager.CollisionManager;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.enums.DirectionEnum;
import com.faust.lhitgame.game.gameentities.enums.EnemyEnum;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.gameentities.impl.SpitterEntity;
import com.faust.lhitgame.game.instances.interfaces.Damager;
import com.faust.lhitgame.game.instances.interfaces.Hurtable;
import com.faust.lhitgame.game.instances.interfaces.Killable;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.Spawner;
import com.faust.lhitgame.game.instances.interfaces.Interactable;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.music.enums.TuneEnum;
import com.faust.lhitgame.game.textbox.manager.TextBoxManager;
import com.faust.lhitgame.screens.GameScreen;

import java.util.Objects;

/**
 * Spitter enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SpitterInstance extends AnimatedInstance implements Interactable, Hurtable, Damager {

    private static final int ATTACK_VALID_FRAME = 6; // Frame to activate attack sensor
    private static final long SPITTING_FREQUENCY_IN_MILLIS = 1650;

    private final TextBoxManager textBoxManager;
    private final MusicManager musicManager;
    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;

    private final Spawner spawner;

    private long startAttackCooldown;

    private boolean isDead = false;
    private boolean canAttack = false;
    private boolean canBeDamaged = false;
    private boolean isAggressive = false;

    public SpitterInstance(float x, float y, AssetManager assetManager, TextBoxManager textBoxManager, Spawner spawner, MusicManager musicManager) {
        super(new SpitterEntity(assetManager));
        currentDirectionEnum = DirectionEnum.DOWN;
        this.startX = x;
        this.startY = y;
        this.textBoxManager = textBoxManager;
        this.spawner = spawner;
        this.startAttackCooldown = TimeUtils.nanoTime();
        this.musicManager = musicManager;
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        //Counting living HiveInstance in room. If 0, SpitterInstance can be damaged
        long hiveCount = roomContent.enemyList.stream().filter(ene -> ene instanceof HiveInstance && !((Killable) ene).isDead()).count();
        canBeDamaged = hiveCount == 0;

        //If one of the HiveInstances are hurted, start aggression
        if (!isAggressive) {
            isAggressive = roomContent.enemyList.stream().anyMatch(ene -> ene instanceof HiveInstance && GameBehavior.HURT.equals(ene.getCurrentBehavior()));
        }

        //Change Music
        if (isAggressive && musicManager.isPlaying(TuneEnum.CHURCH)) {
            musicManager.stopMusic();
            musicManager.playMusic(TuneEnum.FINAL);
        }

        switch (currentBehavior) {
            case ATTACK: {

                attackLogic(stateTime);
                break;
            }
            case IDLE: {
                // Every six seconds spits meat
                if (isAggressive && TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(SPITTING_FREQUENCY_IN_MILLIS)) {
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
        hitBoxShape.setAsBox(6, 12);

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

        Vector2 drawPosition = adjustPosition();
        //Draw Spitter
        // If not hurt or the flickering Spitter must be shown, draw the texture
        if (!mustFlicker || !GameBehavior.HURT.equals(currentBehavior)) {
            batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET);
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
            spawner.spawnInstance(PortalInstance.class, this.startX, this.startY, EnemyEnum.PORTAL.name());
            isDead = true;
            currentBehavior = GameBehavior.DEAD;
        } else if (!GameBehavior.HURT.equals(currentBehavior)) {
            ((SpitterEntity) entity).playHurtCry();

            //If 0 HiveInstance  in room, SpitterInstance can be damaged
            if (!canBeDamaged) {
                textBoxManager.addNewTimedTextBox("warn.spitter.damage");
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

        int currentFrame = ((AnimatedEntity) entity).getFrameIndex(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime));

        //Activate weapon sensor on frame
        if (currentFrame == ATTACK_VALID_FRAME && canAttack) {
            ((SpitterEntity) entity).playSpitSound();
            spawner.spawnInstance(MeatInstance.class, this.startX, this.startY, EnemyEnum.MEAT.name());
            canAttack = false;
        }
        // Resetting Behaviour on animation end
        if (((AnimatedEntity) entity).isAnimationFinished(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime))) {
            currentBehavior = GameBehavior.IDLE;
        }
    }

    private float mapStateTimeFromBehaviour(float stateTime) {

        if (currentBehavior == GameBehavior.ATTACK) {
            return (stateTime - attackDeltaTime);
        }
        return stateTime;
    }

}
