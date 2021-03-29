package faust.lhipgame.game.instances.impl;

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
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.Killable;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.StrixEntity;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.Interactable;
import faust.lhipgame.screens.GameScreen;
import faust.lhipgame.game.world.manager.CollisionManager;

import java.util.Objects;

/**
 * Strix enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class StrixInstance extends AnimatedInstance implements Interactable, Killable {

    private static final float STRIX_SPEED = 30;
    private boolean attachedToPlayer = false;

    private PlayerInstance target;
    private Timer.Task leechLifeTimer;
    private boolean isDead = false;

    public StrixInstance(float x, float y, PlayerInstance target, AssetManager assetManager) {
        super(new StrixEntity(assetManager));
        currentDirection = Direction.DOWN;
        this.startX = x;
        this.startY = y;
        this.target = target;
    }

    @Override
    public void doLogic(float stateTime) {

        hitBox.setTransform(body.getPosition().x, body.getPosition().y + 8, 0);

        if (GameBehavior.HURT.equals(currentBehavior))
            return;

        if (!attachedToPlayer && target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_SIGHT) {
            currentBehavior = GameBehavior.WALK;
            // Normal from strix position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();

            // If not already attached su player
            currentDirection = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(STRIX_SPEED * direction.x, STRIX_SPEED * direction.y);
        } else {

            currentBehavior = attachedToPlayer ? GameBehavior.ATTACK : GameBehavior.IDLE;

            body.setLinearVelocity(0, 0);
        }
    }

    @Override
    public void postHurtLogic() {

        // is pushed away while flickering
        Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                target.getBody().getPosition().y - body.getPosition().y).nor();

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
        return isDead;
    }

    @Override
    public double damageRoll() {
        return 0;
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

        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, currentDirection, stateTime);

        //Draw shadow
        batch.draw(((StrixEntity) entity).getShadowTexture(), body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);

        //Draw Strix
        if (GameBehavior.IDLE.equals(currentBehavior)) {
            // On Idle, the Strix is landed. While walking it flies
            batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - 8 - POSITION_Y_OFFSET);
        } else {

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
        }

    }

    private void leechLife(PlayerInstance playerInstance) {

        // Force cancel another one must start
        if (Objects.nonNull(leechLifeTimer)) {
            leechLifeTimer.cancel();
        }

        //Keep leeching
        leechLifeTimer = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (attachedToPlayer) {
                    playerInstance.hurt(1);
                    leechLife(playerInstance);
                    Gdx.app.log("DEBUG", "END leech timer");
                }
            }
        }, 1);
        Gdx.app.log("DEBUG", "START leech timer");

    }

    public boolean isAttachedToPlayer() {
        return attachedToPlayer;
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        // Start to leech
        attachedToPlayer = true;
        leechLife(playerInstance);

    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        // End leech and cancel timer if present
        attachedToPlayer = false;
        if (Objects.nonNull(leechLifeTimer)) {
            leechLifeTimer.cancel();
            Gdx.app.log("DEBUG", "CANCEL leech timer");
        }
    }

    /**
     * Method for hurting the Strix
     *
     * @param damageReceived to be subtracted
     */
    @Override
    public void hurt(int damageReceived) {
        //Should not be hurted if attached to player!
        if(!isAttachedToPlayer()){
            if (isDying()) {
                isDead = true;
            } else if (!GameBehavior.HURT.equals(currentBehavior)) {
                this.damage += Math.min(getResistance(), damageReceived);
                Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
                postHurtLogic();
            }
        }
    }

    @Override
    public int getResistance() {
        return 9;
    }

}
