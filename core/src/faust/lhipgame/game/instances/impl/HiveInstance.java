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
import faust.lhipgame.game.gameentities.interfaces.Damager;
import faust.lhipgame.game.gameentities.interfaces.Hurtable;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.HiveEntity;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.interfaces.Interactable;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.game.world.manager.CollisionManager;
import faust.lhipgame.screens.GameScreen;

import java.util.Objects;

/**
 * Hive enemy instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class HiveInstance extends AnimatedInstance implements Interactable, Hurtable, Damager {

    private final TextBoxManager textBoxManager;
    private boolean isDead = false;

    public HiveInstance(float x, float y, AssetManager assetManager, TextBoxManager textBoxManager) {
        super(new HiveEntity(assetManager));
        currentDirection = Direction.DOWN;
        this.startX = x;
        this.startY = y;
        this.textBoxManager = textBoxManager;
    }

    @Override
    public void doLogic(float stateTime) {
        // just a static passive enemy

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

        // Hitbox definition
        BodyDef hitBoxDef = new BodyDef();
        hitBoxDef.type = BodyDef.BodyType.DynamicBody;
        hitBoxDef.fixedRotation = true;
        hitBoxDef.position.set(x, y);

        // Define shape
        PolygonShape hitBoxShape = new PolygonShape();
        hitBoxShape.setAsBox(8, 12);

        // Define Fixture
        FixtureDef hitBoxFixtureDef = new FixtureDef();
        hitBoxFixtureDef.shape = hitBoxShape;
        hitBoxFixtureDef.density = 1;
        hitBoxFixtureDef.friction = 1;
        hitBoxFixtureDef.isSensor = true;
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

        //Draw Hive
        // If not hurt or the flickering Hive must be shown, draw the texture
        if (!mustFlicker || !GameBehavior.HURT.equals(currentBehavior)) {
            batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET+4);
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
        if (!isDead()) {
            playerInstance.hurt(this);
        }
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        // End leech and cancel timer if present
    }

    /**
     * Method for hurting the Hive
     *
     * @param attacker
     */
    @Override
    public void hurt(GameInstance attacker) {
        if (isDying()) {
            ((HiveEntity) entity).playDeathCry();
            isDead = true;
            currentBehavior = GameBehavior.DEAD;
        } else if (!GameBehavior.HURT.equals(currentBehavior)) {
            ((HiveEntity) entity).playHurtCry();

            //If Undead or Otherworldly, halve normal lance damage
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
        return 8;
    }

    public double damageRoll() {
        return 0; // harmless! just bounce player
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {
        return stateTime * 0.75f;
    }

}
