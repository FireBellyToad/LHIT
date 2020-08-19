package faust.lhipgame.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.gameentities.LivingEntity;
import faust.lhipgame.gameentities.enums.ItemEnum;
import faust.lhipgame.gameentities.impl.PlayerEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;
import faust.lhipgame.instances.GameInstance;
import faust.lhipgame.instances.LivingInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player Instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class PlayerInstance extends LivingInstance implements InputProcessor {

    private static final float PLAYER_SPEED = 50;
    private static final int EXAMINATION_DISTANCE = 40;
    private static final int ATTACK_VALID_FRAME = 6; // Frame to activate attack sensor
    private static final float SPEAR_SENSOR_Y_OFFSET = 10;
    private static final float HEALTH_KIT_TIME = 4;
    private static final int MAX_AVAILABLE_HEALTH_KIT = 9;

    // Time delta between state and start of attack animation
    private float attackDeltaTime =0;

    //Body for spear attacks
    private Body downSpearBody;
    private Body leftSpearBody;
    private Body rightSpearBody;
    private Body upSpearBody;

    private final List<GameInstance> roomPoiList = new ArrayList();
    private POIInstance nearestPOIInstance;

    private int availableHealthKits = 0; // available Health Kits
    private Timer.Task isHealingTimer;
    private int foundMorgengabe = 0;

    public PlayerInstance() {
        super(new PlayerEntity());
        currentDirection = Direction.DOWN;
        damage = 10;

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void doLogic(float stateTime) {

        // Setting Attack area position
        rightSpearBody.setTransform(body.getPosition().x+10, body.getPosition().y+SPEAR_SENSOR_Y_OFFSET,0);
        upSpearBody.setTransform(body.getPosition().x-4, body.getPosition().y+11+SPEAR_SENSOR_Y_OFFSET,0);
        leftSpearBody.setTransform(body.getPosition().x-10, body.getPosition().y+SPEAR_SENSOR_Y_OFFSET,0);
        downSpearBody.setTransform(body.getPosition().x-4, body.getPosition().y-11+SPEAR_SENSOR_Y_OFFSET,0);

        // Interrupt healing if moving
        if (GameBehavior.KNEE.equals(currentBehavior) &&
                (this.body.getLinearVelocity().x != 0 ||
                this.body.getLinearVelocity().y != 0)) {
            currentBehavior = GameBehavior.WALK;
        }

        // If not healing
        if (!GameBehavior.KNEE.equals(currentBehavior)) {
            if(!Objects.isNull(isHealingTimer)) {
                // Resetting healing timer if healing is interrupted by anything
                isHealingTimer.cancel();
                isHealingTimer = null;
                currentBehavior = GameBehavior.IDLE;
            }

            if (GameBehavior.ATTACK.equals(currentBehavior)) {
                // If attacking do attack logic
                setPlayerLinearVelocity(0, 0);
                attackLogic(stateTime);
            } else {

                // If the player has stopped moving, set idle behaviour
                if (this.body.getLinearVelocity().x == 0 && this.body.getLinearVelocity().y == 0) {
                    currentBehavior = GameBehavior.IDLE;
                } else {
                    currentBehavior = GameBehavior.WALK;
                }

                // Set horizontal direction if horizontal velocity is not zero
                if (this.body.getLinearVelocity().x == PLAYER_SPEED) {
                    this.currentDirection = Direction.RIGHT;
                } else if (this.body.getLinearVelocity().x == -PLAYER_SPEED) {
                    this.currentDirection = Direction.LEFT;
                }

                // Set vertical direction if vertical velocity is not zero
                if (this.body.getLinearVelocity().y == PLAYER_SPEED) {
                    this.currentDirection = Direction.UP;
                } else if (this.body.getLinearVelocity().y == -PLAYER_SPEED) {
                    this.currentDirection = Direction.DOWN;
                }

            }
        }

        // Checking if there is any POI near enough to be examined by the player
        if (!roomPoiList.isEmpty()) {

            roomPoiList.forEach((poi) -> ((POIInstance) poi).setEnableFlicker(false));

            nearestPOIInstance = (POIInstance) this.getNearestInstance(roomPoiList);
            if (nearestPOIInstance.getBody().getPosition().dst(getBody().getPosition()) <= EXAMINATION_DISTANCE) {
                nearestPOIInstance.setEnableFlicker(true);
            }
        }
    }

    @Override
    protected void postHurtLogic() {

    }

    /**
     * Triggers the examination event of the nearest POI found
     */
    private void examineNearestPOI() {
        Objects.requireNonNull(nearestPOIInstance);
        if (nearestPOIInstance.getBody().getPosition().dst(getBody().getPosition()) <= EXAMINATION_DISTANCE) {
            nearestPOIInstance.examine();
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

        TextureRegion frame = ((LivingEntity) entity).getFrame(currentBehavior, currentDirection,
                mapStateTimeFromBehaviour(stateTime));

        int xOffset = 0;
        int yOffset = 0;

        if(GameBehavior.ATTACK.equals(currentBehavior)){
            switch (currentDirection){
                case LEFT:{
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

        //Draw shadow
        batch.draw(((PlayerEntity) entity).getShadowTexture(), body.getPosition().x- POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);

        //Draw Walfrit
        batch.draw(frame, body.getPosition().x - xOffset - POSITION_OFFSET, body.getPosition().y - yOffset- POSITION_Y_OFFSET);

    }

    /**
     * Alter state time for different animation speed based on current behaviour
     * @param stateTime
     * @return
     */
    private float mapStateTimeFromBehaviour(float stateTime) {
        switch (currentBehavior){
            case ATTACK:{
                return 2.25f * (stateTime - attackDeltaTime);
            }
        }
        return stateTime;
    }

    /**
     * Handle the attack logic, activating and deactivating attack collision bodies
     * @param stateTime
     */
    private void attackLogic(float stateTime) {

        if(attackDeltaTime == 0)
            attackDeltaTime = stateTime;


        int currentFrame = ((LivingEntity) entity).getFrameIndex(currentBehavior, currentDirection, mapStateTimeFromBehaviour(stateTime));
        if (currentFrame >= ATTACK_VALID_FRAME && currentFrame < 10) {
            switch (currentDirection){
                case UP:{
                    upSpearBody.setActive(true);
                    break;
                }
                case DOWN:{
                    downSpearBody.setActive(true);
                    break;
                }
                case LEFT:{
                    leftSpearBody.setActive(true);
                    break;
                }
                case RIGHT:{
                    rightSpearBody.setActive(true);
                    break;
                }
            }
        } else {
            rightSpearBody.setActive(false);
            upSpearBody.setActive(false);
            leftSpearBody.setActive(false);
            downSpearBody.setActive(false);
        }

        // Resetting Behaviour on animation end
        if(((LivingEntity) entity).isAnimationFinished(currentBehavior, currentDirection, mapStateTimeFromBehaviour(stateTime))){
            currentBehavior = GameBehavior.IDLE;
        }
    }

    @Override
    public void createBody(World world, float x, float y) {
        {
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

            // Associate body to world
            body = world.createBody(bodyDef);
            body.setUserData(this);
            body.createFixture(mainFixtureDef);
            shape.dispose();


            BodyDef rightSpearDef = new BodyDef();
            rightSpearDef.type = BodyDef.BodyType.KinematicBody;
            rightSpearDef.fixedRotation = true;
            rightSpearDef.position.set(x, y);

            // Define shape
            PolygonShape rightSpearShape = new PolygonShape();
            rightSpearShape.setAsBox(6, 3);

            // Define Fixtures
            FixtureDef rightSpearFixtureDef = new FixtureDef();
            rightSpearFixtureDef.shape = shape;
            rightSpearFixtureDef.density = 1;
            rightSpearFixtureDef.friction = 1;
            rightSpearFixtureDef.isSensor = true;

            // Associate body to world
            rightSpearBody = world.createBody(rightSpearDef);
            rightSpearBody.setUserData(this);
            rightSpearBody.createFixture(rightSpearFixtureDef);
            rightSpearBody.setActive(false);
            rightSpearShape.dispose();

            BodyDef upSpearDef = new BodyDef();
            upSpearDef.type = BodyDef.BodyType.KinematicBody;
            upSpearDef.fixedRotation = true;
            upSpearDef.position.set(x, y);

            // Define shape
            PolygonShape upSpearShape = new PolygonShape();
            upSpearShape.setAsBox(3, 6);

            // Define Fixtures
            FixtureDef upSpearFixtureDef = new FixtureDef();
            upSpearFixtureDef.shape = shape;
            upSpearFixtureDef.density = 1;
            upSpearFixtureDef.friction = 1;
            upSpearFixtureDef.isSensor = true;

            // Associate body to world
            upSpearBody = world.createBody(upSpearDef);
            upSpearBody.setUserData(this);
            upSpearBody.createFixture(upSpearFixtureDef);
            upSpearBody.setActive(false);
            upSpearShape.dispose();

            BodyDef leftSpearDef = new BodyDef();
            leftSpearDef.type = BodyDef.BodyType.KinematicBody;
            leftSpearDef.fixedRotation = true;
            leftSpearDef.position.set(x, y);

            // Define shape
            PolygonShape leftSpearShape = new PolygonShape();
            leftSpearShape.setAsBox(6, 3);

            // Define Fixtures
            FixtureDef leftSpearFixtureDef = new FixtureDef();
            leftSpearFixtureDef.shape = shape;
            leftSpearFixtureDef.density = 1;
            leftSpearFixtureDef.friction = 1;
            leftSpearFixtureDef.isSensor = true;

            // Associate body to world
            leftSpearBody = world.createBody(leftSpearDef);
            leftSpearBody.setUserData(this);
            leftSpearBody.createFixture(leftSpearFixtureDef);
            leftSpearBody.setActive(false);
            leftSpearShape.dispose();

            BodyDef downSpearDef = new BodyDef();
            downSpearDef.type = BodyDef.BodyType.KinematicBody;
            downSpearDef.fixedRotation = true;
            downSpearDef.position.set(x, y);

            // Define shape
            PolygonShape downSpearShape = new PolygonShape();
            downSpearShape.setAsBox(3, 6);

            // Define Fixtures
            FixtureDef downSpearFixtureDef = new FixtureDef();
            downSpearFixtureDef.shape = shape;
            downSpearFixtureDef.density = 1;
            downSpearFixtureDef.friction = 1;
            downSpearFixtureDef.isSensor = true;

            // Associate body to world
            downSpearBody = world.createBody(downSpearDef);
            downSpearBody.setUserData(this);
            downSpearBody.createFixture(downSpearFixtureDef);
            downSpearBody.setActive(false);
            downSpearShape.dispose();

        }

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

        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        // Set instance direction and velocity accordingly to the pressed key
        // Check if not moving in opposite direction
        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && verticalVelocity != -PLAYER_SPEED) {
                    verticalVelocity = PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.S:
            case Input.Keys.DOWN: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && verticalVelocity != PLAYER_SPEED) {
                    verticalVelocity = -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.A:
            case Input.Keys.LEFT: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && horizontalVelocity != PLAYER_SPEED) {
                    horizontalVelocity = -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.D:
            case Input.Keys.RIGHT: {
                if (!GameBehavior.ATTACK.equals(currentBehavior) && horizontalVelocity != -PLAYER_SPEED) {
                    horizontalVelocity = PLAYER_SPEED;
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
            case Input.Keys.J:{
                if (!GameBehavior.ATTACK.equals(currentBehavior)){
                    currentBehavior = GameBehavior.ATTACK;
                    attackDeltaTime = 0;
                }
                break;
            }
            case Input.Keys.C:
            case Input.Keys.L: {
                this.useHealthKit();
                break;
            }
        }

        setPlayerLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

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
     * Walfrit cures himself with a health kit
     */
    private void useHealthKit() {
        //FIXME add timer
        if (Objects.isNull(isHealingTimer) && availableHealthKits > 0 && damage > 0) {
            currentBehavior = GameBehavior.KNEE;
            isHealingTimer = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    availableHealthKits--;
                    isHealingTimer = null;
                    damage = Math.max(0, damage - 4);
                    currentBehavior = GameBehavior.IDLE;
                }
            }, HEALTH_KIT_TIME);

        }
    }

    /**
     * Called whenever the player finds an item
     * @param itemFound
     */
    public void foundItem(ItemEnum itemFound) {
        switch (itemFound){
            case HEALTH_KIT:{
                availableHealthKits = Math.min(MAX_AVAILABLE_HEALTH_KIT,availableHealthKits+1);
                break;
            }
            case MORGENGABE:{
                foundMorgengabe++;
                break;
            }
            default:{
                Gdx.app.log("WARN","No implementation for item" + itemFound.name());
            }
        }
    }

    public int getAvailableHealthKits() {
        return availableHealthKits;
    }

    public int getFoundMorgengabe() {
        return foundMorgengabe;
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
