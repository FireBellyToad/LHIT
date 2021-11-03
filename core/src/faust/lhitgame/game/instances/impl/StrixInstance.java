package faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.interfaces.Damager;
import faust.lhitgame.game.gameentities.interfaces.Hurtable;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.gameentities.impl.StrixEntity;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.instances.GameInstance;
import faust.lhitgame.game.instances.interfaces.Interactable;
import faust.lhitgame.game.rooms.AbstractRoom;
import faust.lhitgame.game.world.manager.CollisionManager;
import faust.lhitgame.screens.GameScreen;

import java.util.Objects;

/**
 * Strix enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class StrixInstance extends AnimatedInstance implements Interactable, Hurtable, Damager {

    private static final float STRIX_SPEED = 35;
    private static final long LEECHING_FREQUENCY_IN_MILLIS = 1000;
    private boolean attachedToPlayer = false;
    private boolean isAggressive = false;

    private final PlayerInstance target;
    private long leechStartTimer;

    public StrixInstance(float x, float y, PlayerInstance target, AssetManager assetManager) {
        super(new StrixEntity(assetManager));
        currentDirectionEnum = DirectionEnum.DOWN;
        this.startX = x;
        this.startY = y;
        this.target = target;
    }

    @Override
    public void doLogic(float stateTime, AbstractRoom currentRoom) {

        hitBox.setTransform(body.getPosition().x, body.getPosition().y + 8, 0);

        if (GameBehavior.HURT.equals(currentBehavior) || GameBehavior.DEAD.equals(currentBehavior))
            return;

        if (!attachedToPlayer && ((target.getBody().getPosition().dst(getBody().getPosition()) <= (LINE_OF_SIGHT * 0.75) && !isAggressive) ||
                (target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_SIGHT && isAggressive))) {
            currentBehavior = GameBehavior.WALK;
            isAggressive = true;
            // Normal from strix position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();

            // If not already attached su player
            currentDirectionEnum = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(STRIX_SPEED * direction.x, STRIX_SPEED * direction.y);
        } else {

            currentBehavior = GameBehavior.IDLE;
            body.setLinearVelocity(0, 0);

            //leech if attachedToPlayer
            if (attachedToPlayer){
                currentBehavior = GameBehavior.ATTACK;
                leechLife();
            } else {
                leechStartTimer = 0;
            }
        }
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {
        return stateTime;
    }

    @Override
    public void postHurtLogic(GameInstance attacker) {

        // is pushed away while flickering
        Vector2 direction = new Vector2(attacker.getBody().getPosition().x - body.getPosition().x,
                attacker.getBody().getPosition().y - body.getPosition().y).nor();

        body.setLinearVelocity(STRIX_SPEED * 4 * -direction.x, STRIX_SPEED * 4 * -direction.y);
        currentBehavior = GameBehavior.HURT;
        attachedToPlayer = false;
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
        return GameBehavior.DEAD.equals(currentBehavior);
    }

    @Override
    public double damageRoll() {
        return 1;
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
        fixtureDef.isSensor = true;

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
        hitBoxShape.setAsBox(4, 6);

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

        Vector2 drawPosition = adjustPosition();
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime));
        batch.begin();
        //Draw shadow
        batch.draw(((StrixEntity) entity).getShadowTexture(), drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET);

        //Draw Strix
        if (GameBehavior.IDLE.equals(currentBehavior) || GameBehavior.DEAD.equals(currentBehavior)) {
            // On Idle, the Strix is landed. While walking it flies
            batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - 8 - POSITION_Y_OFFSET);
        } else {

            // If not hurt or the flickering POI must be shown, draw the texture
            if (!mustFlicker || !GameBehavior.HURT.equals(currentBehavior)) {
                batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET);
            }

            // Every 1/8 seconds alternate between showing and hiding the texture to achieve flickering effect
            if (GameBehavior.HURT.equals(currentBehavior) && TimeUtils.timeSinceNanos(startToFlickTime) > GameScreen.FLICKER_DURATION_IN_NANO / 6) {
                mustFlicker = !mustFlicker;

                // restart flickering timer
                startToFlickTime = TimeUtils.nanoTime();
            }
        }
        batch.end();

    }

    @Override
    public boolean isDisposable() {
        return isDead();
    }

    private void leechLife() {

        // Force cancel another one must start
        if (leechStartTimer == 0) {
            leechStartTimer = TimeUtils.nanoTime();
        }

        //Keep leeching
        if (attachedToPlayer && TimeUtils.timeSinceNanos(leechStartTimer) > TimeUtils.millisToNanos(LEECHING_FREQUENCY_IN_MILLIS)) {
            target.hurt(StrixInstance.this);
            //Prevents loop on gameover screen
            if(target.isDead()){
                ((StrixEntity) entity).stopLeechSound();
            }
            leechStartTimer = TimeUtils.nanoTime();
            Gdx.app.log("DEBUG", "END leech timer");
        }
        Gdx.app.log("DEBUG", "START leech timer");

    }

    public boolean isAttachedToPlayer() {
        return attachedToPlayer;
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        // Start to leech
        attachedToPlayer = true;
        ((StrixEntity) entity).playLeechSound();

    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        // End leech and cancel timer if present
        attachedToPlayer = false;
        ((StrixEntity) entity).stopLeechSound();
        if (leechStartTimer > 0) {
            leechStartTimer = 0;
            Gdx.app.log("DEBUG", "CANCEL leech timer");
        }
    }

    /**
     * Method for hurting the Strix
     *
     * @param attacker
     */
    @Override
    public void hurt(GameInstance attacker) {
        Objects.requireNonNull(attacker);

        //Should not be hurted if attached to player!
        if(!isAttachedToPlayer()){
            
            if (isDying()) {
                ((StrixEntity) entity).playDeathCry();
                body.setLinearVelocity(0, 0);
                currentBehavior = GameBehavior.DEAD;
            } else if (!GameBehavior.HURT.equals(currentBehavior)) {
                ((StrixEntity) entity).playHurtCry();
                // Hurt by player
                this.damage += ((Damager)attacker).damageRoll();
                Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
                postHurtLogic(attacker);
            }
        }
    }

    @Override
    public int getResistance() {
        return 9;
    }

}
