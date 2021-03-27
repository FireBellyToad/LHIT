package faust.lhipgame.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import faust.lhipgame.gameentities.AnimatedEntity;
import faust.lhipgame.gameentities.Killable;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;
import faust.lhipgame.gameentities.impl.BoundedEntity;
import faust.lhipgame.gameentities.impl.BoundedEntity;
import faust.lhipgame.instances.AnimatedInstance;
import faust.lhipgame.instances.Interactable;
import faust.lhipgame.screens.GameScreen;
import faust.lhipgame.world.manager.CollisionManager;

import java.util.Objects;

/**
 * Bounded enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class BoundedInstance extends AnimatedInstance implements Interactable, Killable {

    private static final float BOUNDED_SPEED = 30;
    private static final int LINE_OF_ATTACK = 30;
    private static final int LINE_OF_SIGHT = 70;

    private PlayerInstance target;
    private boolean isDead = false;

    public BoundedInstance(float x, float y, PlayerInstance target, AssetManager assetManager) {
        super(new BoundedEntity(assetManager));
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

        if (target.getBody().getPosition().dst(getBody().getPosition()) > LINE_OF_ATTACK &&
                target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_SIGHT) {
            currentBehavior = GameBehavior.WALK;
            // Normal from strix position to target
            Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                    target.getBody().getPosition().y - body.getPosition().y).nor();

            // If not already attached su player
            currentDirection = extractDirectionFromNormal(direction);

            // Move towards target
            body.setLinearVelocity(BOUNDED_SPEED * direction.x, BOUNDED_SPEED * direction.y);
        } else if (target.getBody().getPosition().dst(getBody().getPosition()) <= LINE_OF_ATTACK) {
            //TODO attack logic
            currentBehavior = GameBehavior.IDLE;

            body.setLinearVelocity(0, 0);
        } else {
            currentBehavior = GameBehavior.IDLE;

            body.setLinearVelocity(0, 0);
        }
    }

    @Override
    public void postHurtLogic() {

        // is pushed away while flickering
        Vector2 direction = new Vector2(target.getBody().getPosition().x - body.getPosition().x,
                target.getBody().getPosition().y - body.getPosition().y).nor();

        body.setLinearVelocity(BOUNDED_SPEED * 4 * -direction.x, BOUNDED_SPEED * 4 * -direction.y);
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
        shape.setAsBox(4, 2);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;

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

        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, currentDirection, stateTime);

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
     * @param damageReceived to be subtracted
     */
    @Override
    public void hurt(int damageReceived) {
        if (isDying()) {
            isDead = true;
        } else if (!GameBehavior.HURT.equals(currentBehavior)) {
            this.damage += Math.min(getResistance(), damageReceived);
            Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
            postHurtLogic();
        }
    }

    @Override
    public int getResistance() {
        return 9;
    }

    public double damageRoll() {
        return MathUtils.random(1, 6);
    }

}