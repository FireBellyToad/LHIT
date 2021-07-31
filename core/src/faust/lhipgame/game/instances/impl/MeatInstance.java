package faust.lhipgame.game.instances.impl;

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
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.MeatEntity;
import faust.lhipgame.game.gameentities.interfaces.Damager;
import faust.lhipgame.game.gameentities.interfaces.Hurtable;
import faust.lhipgame.game.gameentities.interfaces.Killable;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.interfaces.Interactable;
import faust.lhipgame.game.world.manager.CollisionManager;

import java.util.Objects;

/**
 * Meat enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MeatInstance extends AnimatedInstance implements Interactable, Damager, Killable {

    private static final float MEAT_SPEED = 60;
    
    private final Vector2 target; // Target x and y;
    private long startAttackCooldown = 0;

    public MeatInstance(float x, float y, PlayerInstance playerInstance, AssetManager assetManager) {
        super(new MeatEntity(assetManager));
        currentDirection = Direction.DOWN;
        this.startX = x;
        this.startY = y;

        target = playerInstance.getBody().getPosition().cpy();
        currentBehavior = GameBehavior.WALK;
    }

    @Override
    public void doLogic(float stateTime) {

        switch (currentBehavior) {
            case ATTACK: {
                if (TimeUtils.timeSinceNanos(startAttackCooldown) > TimeUtils.millisToNanos(3000)) {
                    currentBehavior = GameBehavior.DEAD;
                    dispose();
                }
                break;
            }
            case WALK: {
                Vector2 direction = new Vector2(target.x - body.getPosition().x, target.y - body.getPosition().y).nor();

                currentDirection = extractDirectionFromNormal(direction);

                // Move towards target
                body.setLinearVelocity(MEAT_SPEED * direction.x, MEAT_SPEED * direction.y);

                // If near target, starts attacking
                if(target.dst(getBody().getPosition()) < 5){
                    currentBehavior = GameBehavior.ATTACK;
                    startAttackCooldown = TimeUtils.nanoTime();
                    body.setLinearVelocity(0,0);
                }
                break;
            }
            case IDLE:{
                // TODO reset position
                body.setLinearVelocity(0,0);
                break;
            }
            default:{
                throw new GdxRuntimeException("Unexpected MeatInstance behaviour!");
            }
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
        shape.setAsBox(8, 2);

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
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, mapStateTimeFromBehaviour(stateTime), true);
        batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);
        batch.end();
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
        return 2; // harmless! just bounce player
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {
        return stateTime * 0.75f;
    }

    @Override
    public boolean isDying() {
        //is never dying...
        return false;
    }

    @Override
    public boolean isDead() {
        return GameBehavior.DEAD.equals(currentBehavior);
    }
}
