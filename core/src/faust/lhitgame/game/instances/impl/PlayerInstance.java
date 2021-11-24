package faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.gameentities.enums.ItemEnum;
import faust.lhitgame.game.gameentities.impl.PlayerEntity;
import faust.lhitgame.game.gameentities.interfaces.Damager;
import faust.lhitgame.game.gameentities.interfaces.Hurtable;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.instances.GameInstance;
import faust.lhitgame.game.rooms.AbstractRoom;
import faust.lhitgame.utils.ShaderWrapper;
import faust.lhitgame.game.world.manager.CollisionManager;
import faust.lhitgame.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player Instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class PlayerInstance extends AnimatedInstance implements InputProcessor, Hurtable, Damager {

    private static final float PLAYER_SPEED = 50;
    private static final float PLAYER_SPEED_SUBMERGED = 40;
    private static final int EXAMINATION_DISTANCE = 40;
    private static final int ATTACK_VALID_FRAME = 6; // Frame to activate attack sensor
    private static final float SPEAR_SENSOR_Y_OFFSET = 8;
    private static final long HEALTH_KIT_TIME_IN_MILLIS = 4000;
    private static final int MAX_AVAILABLE_HEALTH_KIT = 3;

    // Time delta between state and start of attack animation
    private float attackDeltaTime = 0;

    //Body for spear attacks
    private Body downSpearBody;
    private Body leftSpearBody;
    private Body rightSpearBody;
    private Body upSpearBody;

    private final List<GameInstance> roomPoiList = new ArrayList<>();
    private POIInstance nearestPOIInstance;

    private int availableHealthKits = 0; // available Health Kits
    private long startHealingTime;
    private int foundCrosses = 0;
    private int holyLancePieces = 0;
    private int herbsFound = 0;

    private boolean hasArmor = false;
    private boolean isSubmerged = false;
    private boolean isDead = false;
    private boolean prepareEndgame = false;
    private boolean isChangingRoom = false;
    private boolean goToGameOver = false;
    private boolean pauseGame = false;

    private final ParticleEffect waterWalkEffect;

    public PlayerInstance(AssetManager assetManager) {
        super(new PlayerEntity(assetManager));

        currentDirectionEnum = DirectionEnum.DOWN;

        Gdx.input.setInputProcessor(this);

        // Init waterwalk effect
        waterWalkEffect = new ParticleEffect();
        // First is particle configuration, second is particle sprite path (file is embeeded in configuration)
        waterWalkEffect.load(Gdx.files.internal("particles/waterwalk_test"), Gdx.files.internal("sprites/"));
        waterWalkEffect.start();
    }

    @Override
    public int getResistance() {
        return 6 + (hasArmor ? 2 : 0);
    }

    @Override
    public void doLogic(float stateTime, AbstractRoom currentRoom) {

        translateAccessoryBodies();
        waterWalkEffect.getEmitters().first().setPosition(body.getPosition().x, body.getPosition().y);

        //if is Dead, play death animation and then game over
        if (isDead()) {
            isChangingRoom = true;
            setPlayerLinearVelocity(0, 0);
            leftSpearBody.setActive(false);
            upSpearBody.setActive(false);
            downSpearBody.setActive(false);
            rightSpearBody.setActive(false);
            //Init delta time
            if (!GameBehavior.DEAD.equals(currentBehavior)) {
                currentBehavior = GameBehavior.DEAD;
                attackDeltaTime = stateTime;
                ((PlayerEntity) entity).playDeathCry();
            } else if (((PlayerEntity) entity).isAnimationFinished(currentBehavior, mapStateTimeFromBehaviour(stateTime))) {
                goToGameOver = true;
            }
            //Do not do anything else
            return;
        }

        //In endgame do nothing
        if (isPrepareEndgame()) {
            currentBehavior = GameBehavior.IDLE;
            setPlayerLinearVelocity(0, 0);
            return;
        }

        //In roomchange, idling and do nothing
        if (isChangingRoom) {
            currentBehavior = GameBehavior.IDLE;
            return;
        }

        //If hurt, deactivate hitbox and don't do anything
        hitBox.setActive(!GameBehavior.HURT.equals(currentBehavior));
        if (GameBehavior.HURT.equals(currentBehavior))
            return;

        // Interrupt healing if moving
        if (GameBehavior.KNEE.equals(currentBehavior)) {
            if (TimeUtils.timeSinceNanos(startHealingTime) >= TimeUtils.millisToNanos(HEALTH_KIT_TIME_IN_MILLIS)) {
                availableHealthKits--;
                startHealingTime = 0;
                damage = 0;
                currentBehavior = GameBehavior.IDLE;
            }
            if (this.body.getLinearVelocity().x != 0 ||
                    this.body.getLinearVelocity().y != 0) {
                currentBehavior = GameBehavior.WALK;
            }
        }

        // If not healing
        if (!GameBehavior.KNEE.equals(currentBehavior)) {
            if (startHealingTime > 0) {
                // Resetting healing timer if healing is interrupted by anything
                startHealingTime = 0;
                currentBehavior = GameBehavior.IDLE;
            }

            if (GameBehavior.ATTACK.equals(currentBehavior)) {
                // If attacking do attack logic
                setPlayerLinearVelocity(0, 0);
                attackLogic(stateTime);
            } else {
                deactivateAttackBodies();

                // If the player has stopped moving, set idle behaviour
                if (this.body.getLinearVelocity().x == 0 && this.body.getLinearVelocity().y == 0) {
                    currentBehavior = GameBehavior.IDLE;
                } else {
                    currentBehavior = GameBehavior.WALK;
                }

                // Set horizontal direction if horizontal velocity is not zero
                if (this.body.getLinearVelocity().x == PLAYER_SPEED || this.body.getLinearVelocity().x == PLAYER_SPEED_SUBMERGED) {
                    this.currentDirectionEnum = DirectionEnum.RIGHT;
                } else if (this.body.getLinearVelocity().x == -PLAYER_SPEED || this.body.getLinearVelocity().x == -PLAYER_SPEED_SUBMERGED) {
                    this.currentDirectionEnum = DirectionEnum.LEFT;
                }

                // Set vertical direction if vertical velocity is not zero
                if (this.body.getLinearVelocity().y == PLAYER_SPEED || this.body.getLinearVelocity().y == PLAYER_SPEED_SUBMERGED) {
                    this.currentDirectionEnum = DirectionEnum.UP;
                } else if (this.body.getLinearVelocity().y == -PLAYER_SPEED || this.body.getLinearVelocity().y == -PLAYER_SPEED_SUBMERGED) {
                    this.currentDirectionEnum = DirectionEnum.DOWN;
                }

            }
        }

        // Checking if there is any unexamined POI near enough to be examined by the player
        if (!roomPoiList.isEmpty()) {

            roomPoiList.forEach((poi) -> ((POIInstance) poi).setEnableFlicker(false));

            nearestPOIInstance = (POIInstance) this.getNearestInstance(roomPoiList);
            if (nearestPOIInstance.getBody().getPosition().dst(getBody().getPosition()) <= EXAMINATION_DISTANCE &&
                    !nearestPOIInstance.isAlreadyExamined()) {
                nearestPOIInstance.setEnableFlicker(true);
            }
        }
    }

    /**
     * Translate all accessory body
     */
    private void translateAccessoryBodies() {
        rightSpearBody.setTransform(body.getPosition().x + 13, body.getPosition().y + SPEAR_SENSOR_Y_OFFSET, 0);
        upSpearBody.setTransform(body.getPosition().x - 4, body.getPosition().y + 13 + SPEAR_SENSOR_Y_OFFSET, 0);
        leftSpearBody.setTransform(body.getPosition().x - 13, body.getPosition().y + SPEAR_SENSOR_Y_OFFSET, 0);
        downSpearBody.setTransform(body.getPosition().x - 4, body.getPosition().y - 14 + SPEAR_SENSOR_Y_OFFSET, 0);
        hitBox.setTransform(body.getPosition().x, body.getPosition().y + 8, 0);
    }

    @Override
    public void hurt(GameInstance attacker) {

        double damageReceived = ((Damager) attacker).damageRoll();
        if (damageReceived > 0 && isDying()) {
            isDead = true;
        } else if (!GameBehavior.HURT.equals(currentBehavior)) {
            ((PlayerEntity) entity).playHurtCry();
            this.damage += Math.min(getResistance(), damageReceived);
            Gdx.app.log("DEBUG", "Instance " + this.getClass().getSimpleName() + " total damage " + damage);
            postHurtLogic(attacker);
        }

    }

    @Override
    public void postHurtLogic(GameInstance attacker) {
        // is pushed away while flickering if not attacked by Strix
        Vector2 direction = new Vector2(attacker.getBody().getPosition().x - body.getPosition().x,
                attacker.getBody().getPosition().y - body.getPosition().y).nor();

        if (!(attacker instanceof StrixInstance)) {
            body.setLinearVelocity(PLAYER_SPEED * 2 * -direction.x, PLAYER_SPEED * 2 * -direction.y);
        }
        currentBehavior = GameBehavior.HURT;
        // Do nothing for half second
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                currentBehavior = GameBehavior.IDLE;
                body.setLinearVelocity(0, 0);
            }
        }, 0.20f);
    }

    /**
     * Triggers the examination event of the nearest POI found
     */
    private void examineNearestPOI() {
        Objects.requireNonNull(nearestPOIInstance);
        if (nearestPOIInstance.getBody().getPosition().dst(getBody().getPosition()) <= EXAMINATION_DISTANCE &&
                !nearestPOIInstance.isAlreadyExamined()) {
            nearestPOIInstance.examine(this);
        }
    }

    /**
     * Changed the POI list that the Player Instance must check
     *
     * @param poiNewList
     */
    public void changePOIList(List<POIInstance> poiNewList) {
        Objects.requireNonNull(poiNewList);
        roomPoiList.clear();
        roomPoiList.addAll(poiNewList);
        if (!roomPoiList.isEmpty()) {
            nearestPOIInstance = (POIInstance) this.getNearestInstance(roomPoiList);
        }
    }

    /**
     * Handles player bodies movement
     *
     * @param horizontalVelocity
     * @param verticalVelocity
     */
    private void setPlayerLinearVelocity(float horizontalVelocity, float verticalVelocity) {

        this.body.setLinearVelocity(horizontalVelocity, verticalVelocity);
    }

    public void stopAll() {
        this.currentBehavior = GameBehavior.IDLE;
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

        int xOffset = 0;
        int yOffset = 0;

        if (GameBehavior.ATTACK.equals(currentBehavior)) {
            switch (currentDirectionEnum) {
                case LEFT: {
                    xOffset = 1;
                    yOffset = 0;
                    break;
                }
                case RIGHT: {
                    xOffset = -1;
                    yOffset = 1;
                    break;
                }
                case UP: {
                    xOffset = 0;
                    yOffset = -3;
                    break;
                }
            }
        }


        //Draw Walfrit
        // If not hurt or the flickering POI must be shown, draw the texture
        if (!mustFlicker || !GameBehavior.HURT.equals(currentBehavior)) {

            drawWalfritShaded(batch, stateTime, xOffset, yOffset);
        }

        // Every 1/8 seconds alternate between showing and hiding the texture to achieve flickering effect
        if (GameBehavior.HURT.equals(currentBehavior) && TimeUtils.timeSinceNanos(startToFlickTime) > GameScreen.FLICKER_DURATION_IN_NANO / 6) {
            mustFlicker = !mustFlicker;

            // restart flickering timer
            startToFlickTime = TimeUtils.nanoTime();
        }
        batch.end();

    }

    /**
     * Draw Walfrit sprites handling shading
     *
     * @param batch
     * @param stateTime
     * @param xOffset
     * @param yOffset
     */
    private void drawWalfritShaded(SpriteBatch batch, float stateTime, int xOffset, int yOffset) {

        // Get frame (looping if is not dead)
        TextureRegion frame = ((PlayerEntity) entity).getFrame(currentBehavior, currentDirectionEnum,
                mapStateTimeFromBehaviour(stateTime), !GameBehavior.DEAD.equals(currentBehavior));

        ShaderWrapper shader = ((PlayerEntity) entity).getPlayerShader();
        shader.addFlag("hasArmor", hasArmor);
        shader.addFlag("hasHolyLance", holyLancePieces == 2);
        shader.setShaderOnBatchWithFlags(batch);

        Vector2 drawPosition = adjustPosition();
        //Draw shadow
        batch.draw(((PlayerEntity) entity).getShadowTexture(), drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET);

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

        batch.draw(frame, drawPosition.x - xOffset - POSITION_OFFSET, drawPosition.y - yOffset - POSITION_Y_OFFSET);

        //Restore default shader
        shader.resetDefaultShader(batch);
    }

    /**
     * Alter state time for different animation speed based on current behaviour
     *
     * @param stateTime
     * @return
     */
    protected float mapStateTimeFromBehaviour(float stateTime) {
        switch (currentBehavior) {
            case DEAD: {
                return stateTime - attackDeltaTime;
            }
            case ATTACK: {
                return 2.25f * (stateTime - attackDeltaTime);
            }
            default: {
                return stateTime;
            }
        }
    }

    /**
     * Handle the attack logic, activating and deactivating attack collision bodies
     *
     * @param stateTime
     */
    private void attackLogic(float stateTime) {

        if (attackDeltaTime == 0)
            attackDeltaTime = stateTime;

        //Activate spear bodies when in right frame
        final int currentFrame = ((AnimatedEntity) entity).getFrameIndex(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime));
        if (currentFrame >= ATTACK_VALID_FRAME && currentFrame < ATTACK_VALID_FRAME + 4) {
            switch (currentDirectionEnum) {
                case UP: {
                    upSpearBody.setActive(true);
                    break;
                }
                case DOWN: {
                    downSpearBody.setActive(true);
                    break;
                }
                case LEFT: {
                    leftSpearBody.setActive(true);
                    break;
                }
                case RIGHT: {
                    rightSpearBody.setActive(true);
                    break;
                }
            }
        } else {
            deactivateAttackBodies();
        }

        // Resetting Behaviour on animation end
        if (((AnimatedEntity) entity).isAnimationFinished(currentBehavior, currentDirectionEnum, mapStateTimeFromBehaviour(stateTime))) {
            currentBehavior = GameBehavior.IDLE;
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
        shape.setAsBox(4, 2);

        // Define Fixtures
        FixtureDef mainFixtureDef = new FixtureDef();
        mainFixtureDef.shape = shape;
        mainFixtureDef.density = 1;
        mainFixtureDef.friction = 1;
        mainFixtureDef.filter.categoryBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);

        body.setUserData(this);
        body.createFixture(mainFixtureDef);
        shape.dispose();

        BodyDef rightSpearDef = new BodyDef();
        rightSpearDef.type = BodyDef.BodyType.KinematicBody;
        rightSpearDef.fixedRotation = true;
        rightSpearDef.position.set(x + 2, y);

        // Define shape
        PolygonShape rightSpearShape = new PolygonShape();
        rightSpearShape.setAsBox(4, 2);

        // Define Fixtures
        FixtureDef rightSpearFixtureDef = new FixtureDef();
        rightSpearFixtureDef.shape = rightSpearShape;
        rightSpearFixtureDef.density = 1;
        rightSpearFixtureDef.friction = 1;
        rightSpearFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        rightSpearFixtureDef.filter.maskBits = CollisionManager.ENEMY_GROUP;

        // Associate body to world
        rightSpearBody = world.createBody(rightSpearDef);
        rightSpearBody.setUserData(this);
        rightSpearBody.createFixture(rightSpearFixtureDef);
        rightSpearBody.setActive(false);
        rightSpearShape.dispose();

        BodyDef upSpearDef = new BodyDef();
        upSpearDef.type = BodyDef.BodyType.KinematicBody;
        upSpearDef.fixedRotation = true;
        upSpearDef.position.set(x, y - 2);

        // Define shape
        PolygonShape upSpearShape = new PolygonShape();
        upSpearShape.setAsBox(2, 4);

        // Define Fixtures
        FixtureDef upSpearFixtureDef = new FixtureDef();
        upSpearFixtureDef.shape = upSpearShape;
        upSpearFixtureDef.density = 1;
        upSpearFixtureDef.friction = 1;
        upSpearFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        upSpearFixtureDef.filter.maskBits = CollisionManager.ENEMY_GROUP;

        // Associate body to world
        upSpearBody = world.createBody(upSpearDef);
        upSpearBody.setUserData(this);
        upSpearBody.createFixture(upSpearFixtureDef);
        upSpearBody.setActive(false);
        upSpearShape.dispose();

        BodyDef leftSpearDef = new BodyDef();
        leftSpearDef.type = BodyDef.BodyType.KinematicBody;
        leftSpearDef.fixedRotation = true;
        leftSpearDef.position.set(x - 2, y);

        // Define shape
        PolygonShape leftSpearShape = new PolygonShape();
        leftSpearShape.setAsBox(4, 2);

        // Define Fixtures
        FixtureDef leftSpearFixtureDef = new FixtureDef();
        leftSpearFixtureDef.shape = leftSpearShape;
        leftSpearFixtureDef.density = 1;
        leftSpearFixtureDef.friction = 1;
        leftSpearFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        leftSpearFixtureDef.filter.maskBits = CollisionManager.ENEMY_GROUP;

        // Associate body to world
        leftSpearBody = world.createBody(leftSpearDef);
        leftSpearBody.setUserData(this);
        leftSpearBody.createFixture(leftSpearFixtureDef);
        leftSpearBody.setActive(false);
        leftSpearShape.dispose();

        BodyDef downSpearDef = new BodyDef();
        downSpearDef.type = BodyDef.BodyType.KinematicBody;
        downSpearDef.fixedRotation = true;
        downSpearDef.position.set(x, y + 2);

        // Define shape
        PolygonShape downSpearShape = new PolygonShape();
        downSpearShape.setAsBox(2, 4);

        // Define Fixtures
        FixtureDef downSpearFixtureDef = new FixtureDef();
        downSpearFixtureDef.shape = downSpearShape;
        downSpearFixtureDef.density = 1;
        downSpearFixtureDef.friction = 1;
        downSpearFixtureDef.filter.categoryBits = CollisionManager.WEAPON_GROUP;
        downSpearFixtureDef.filter.maskBits = CollisionManager.ENEMY_GROUP;

        // Associate body to world
        downSpearBody = world.createBody(downSpearDef);
        downSpearBody.setUserData(this);
        downSpearBody.createFixture(downSpearFixtureDef);
        downSpearBody.setActive(false);
        downSpearShape.dispose();

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
        hitBoxFixtureDef.filter.categoryBits = CollisionManager.PLAYER_GROUP;
        hitBoxFixtureDef.filter.maskBits = CollisionManager.WEAPON_GROUP;

        // Associate body to world
        hitBox = world.createBody(hitBoxDef);
        hitBox.setUserData(this);
        hitBox.createFixture(hitBoxFixtureDef);
        hitBoxShape.dispose();
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    //----------------------------------------------- CONTROLS -----------------------------------------------

    @Override
    public boolean keyDown(int keycode) {

        if (isPrepareEndgame()) {
            setPlayerLinearVelocity(0, 0);
            return false;
        }

        // If hurt o dying, can't do anything
        if (GameBehavior.HURT.equals(currentBehavior) || GameBehavior.DEAD.equals(currentBehavior)) {
            return false;
        }

        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        // Set instance direction and velocity accordingly to the pressed key
        // Check if not moving in opposite direction
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && verticalVelocity != -PLAYER_SPEED) {
                    verticalVelocity = isSubmerged ? PLAYER_SPEED_SUBMERGED : PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.S:
            case Input.Keys.DOWN: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && verticalVelocity != PLAYER_SPEED) {
                    verticalVelocity = isSubmerged ? -PLAYER_SPEED_SUBMERGED : -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.A:
            case Input.Keys.LEFT: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && horizontalVelocity != PLAYER_SPEED) {
                    horizontalVelocity = isSubmerged ? -PLAYER_SPEED_SUBMERGED : -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.D:
            case Input.Keys.RIGHT: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && horizontalVelocity != -PLAYER_SPEED) {
                    horizontalVelocity = isSubmerged ? PLAYER_SPEED_SUBMERGED : PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.X:
            case Input.Keys.K: {
                if (Objects.nonNull(nearestPOIInstance)) {
                    examineNearestPOI();
                    horizontalVelocity = 0;
                    verticalVelocity = 0;
                }
                break;
            }
            case Input.Keys.Z:
            case Input.Keys.J: {
                //Attacks
                if (!GameBehavior.ATTACK.equals(currentBehavior)) {
                    ((PlayerEntity) entity).playLanceSwing();
                    currentBehavior = GameBehavior.ATTACK;
                    attackDeltaTime = 0;
                }
                break;
            }
            case Input.Keys.C:
            case Input.Keys.L: {
                //Tries to heal himself
                this.useHealthKit();
                break;
            }
            case Input.Keys.ESCAPE:
            case Input.Keys.P: {
                //Pause game
                pauseGame = true;
                break;
            }
        }

        setPlayerLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        // Do not handle anything if is in endgame
        if (isPrepareEndgame()) {
            return false;
        }

        // If hurt, can't do anything
        if (GameBehavior.HURT.equals(currentBehavior)) {
            return false;
        }
        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        // Determine new velocity
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.S:
            case Input.Keys.UP:
            case Input.Keys.DOWN: {
                verticalVelocity = 0;
                break;
            }
            case Input.Keys.A:
            case Input.Keys.D:
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT: {
                horizontalVelocity = 0;
                break;
            }
        }
        setPlayerLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    /**
     * Walfrit cures himself with a health kit, instancing a isHealingTimer
     */
    private void useHealthKit() {
        //If has at least one healing kit, is not already curing himself and at least 1 damage point
        if (startHealingTime == 0 && availableHealthKits > 0 && damage > 0) {
            //Cures himself if not interrupted
            currentBehavior = GameBehavior.KNEE;
            startHealingTime = TimeUtils.nanoTime();
        }
    }

    /**
     * Called whenever the player finds an item
     *
     * @param itemFound
     */
    public void foundItem(ItemEnum itemFound) {

        ((PlayerEntity) entity).playBonusSound();

        switch (itemFound) {
            case HEALTH_KIT: {
                //Increase available Kits, max 3
                availableHealthKits = Math.min(MAX_AVAILABLE_HEALTH_KIT, availableHealthKits + 1);
                herbsFound++;
                break;
            }
            case MORGENGABE: {
                //Increase found Morgangabes
                foundCrosses++;
                break;
            }
            case HOLY_LANCE: {
                //Increase found holy lance pieces
                holyLancePieces++;
                break;
            }
            case ARMOR: {
                // Find armor
                hasArmor = true;
                break;
            }
            default: {
                Gdx.app.log("WARN", "No implementation for item" + itemFound.name());
            }
        }
    }

    /**
     * @return current available Health kits
     */
    public int getAvailableHealthKits() {
        return availableHealthKits;
    }

    public void setAvailableHealthKits(int availableHealthKits) {
        this.availableHealthKits = availableHealthKits;
    }

    /**
     * @return the current number of found crosses
     */
    public int getFoundCrosses() {
        return foundCrosses;
    }

    /**
     * @return if has found armor
     */
    public boolean hasArmor() {
        return hasArmor;
    }

    public void setFoundCrosses(int foundCrosses) {
        this.foundCrosses = foundCrosses;
    }

    /**
     * @return healing timer instance, non null if the player is curing himself
     */
    public long getStartHealingTime() {
        return startHealingTime;
    }

    public int getHolyLancePieces() {
        return holyLancePieces;
    }

    public void setHolyLancePieces(int holyLancePieces) {
        this.holyLancePieces = holyLancePieces;
    }

    public void setHasArmor(boolean armor) {
        this.hasArmor = armor;
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


    public int getDamageDelta() {
        return getResistance() - damage;
    }

    public void cleanReferences() {
        nearestPOIInstance = null;
        //Reset particle emitter and change position
        final ParticleEmitter firstEmitter = waterWalkEffect.getEmitters().first();
        firstEmitter.setPosition(startX, startY);
        firstEmitter.reset();
    }

    public void setSubmerged(boolean submerged) {
        Vector2 velocity = this.body.getLinearVelocity();
        if (!isChangingRoom && submerged && !isSubmerged) {
            //Play sound when Walfrit gets in water
            ((PlayerEntity) entity).playWaterSplash();

            //Restore water speed
            velocity.x = (velocity.x / PLAYER_SPEED) * PLAYER_SPEED_SUBMERGED;
            velocity.y = (velocity.y / PLAYER_SPEED) * PLAYER_SPEED_SUBMERGED;
        } else if (!submerged) {

            //Restore terrain speed
            velocity.x = (velocity.x / PLAYER_SPEED_SUBMERGED) * PLAYER_SPEED;
            velocity.y = (velocity.y / PLAYER_SPEED_SUBMERGED) * PLAYER_SPEED;
        }

        //Clamp speed if over the maximum one
        setPlayerLinearVelocity(MathUtils.clamp(velocity.x ,-PLAYER_SPEED,PLAYER_SPEED),
                MathUtils.clamp(velocity.y,-PLAYER_SPEED,PLAYER_SPEED));

        isSubmerged = submerged;
    }

    public double damageRoll() {
        return Math.max(MathUtils.random(1, 6), MathUtils.random(1, 6));
    }

    @Override
    public void dispose() {
        super.dispose();
        rightSpearBody.getFixtureList().forEach(f ->
                rightSpearBody.destroyFixture(f));
        upSpearBody.getFixtureList().forEach(f ->
                upSpearBody.destroyFixture(f));
        leftSpearBody.getFixtureList().forEach(f ->
                leftSpearBody.destroyFixture(f));
        downSpearBody.getFixtureList().forEach(f ->
                downSpearBody.destroyFixture(f));
        waterWalkEffect.dispose();
    }

    @Override
    //Player is never disposable
    public boolean isDisposable() {
        return false;
    }

    /**
     * Deactivate all attacker bodies
     */
    private void deactivateAttackBodies() {
        rightSpearBody.setActive(false);
        upSpearBody.setActive(false);
        leftSpearBody.setActive(false);
        downSpearBody.setActive(false);
    }

    /**
     * @return true if must go to gameover
     */
    public boolean goToGameOver() {
        return goToGameOver;
    }

    /**
     * @return true if game is ending
     */
    public boolean isPrepareEndgame() {
        return prepareEndgame;
    }

    public void setPrepareEndgame(boolean prepareEndgame) {
        this.prepareEndgame = prepareEndgame;
    }

    public void setChangingRoom(boolean changingRoom) {
        isChangingRoom = changingRoom;
    }

    /**
     * Reset this PlayerInstance as inputprocessor
     */
    public void setAsInputProcessor() {
        pauseGame = false;
        currentBehavior = GameBehavior.IDLE;
        setPlayerLinearVelocity(0, 0);
        Gdx.input.setInputProcessor(this);
    }

    public void setHerbsFound(int herbsFound) {
        this.herbsFound = herbsFound;
    }

    public int getHerbsFound() {
        return herbsFound;
    }

    public boolean isPauseGame() {
        return pauseGame;
    }
    public void setPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
