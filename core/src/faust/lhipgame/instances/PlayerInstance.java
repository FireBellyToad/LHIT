package faust.lhipgame.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.gameentities.PlayerEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player Instance class
 */
public class PlayerInstance extends LivingInstance implements InputProcessor {

    private static final float PLAYER_SPEED = 50;
    private static final int EXAMINATION_DISTANCE = 40;

    private final List<GameInstance> roomPoiList = new ArrayList();
    private POIInstance nearestPOIInstance;

    public PlayerInstance() {
        super(new PlayerEntity());
        currentDirection = Direction.DOWN;

        Gdx.input.setInputProcessor((InputProcessor) this);
    }

    @Override
    public void doLogic() {

        // If the player has stopped moving, set idle behaviour
        if(this.body.getLinearVelocity().x == 0 && this.body.getLinearVelocity().y == 0){
            this.currentBehavior = GameBehavior.IDLE;
        } else {
            this.currentBehavior = GameBehavior.WALK;
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


        // Checking if there is any POI near enough to be examined by the player
        if (!roomPoiList.isEmpty()) {

            roomPoiList.forEach((poi) ->  ((POIInstance) poi).setEnableFlicker(false));
            
            nearestPOIInstance = (POIInstance) this.getNearestInstance(roomPoiList);
            if (nearestPOIInstance.getBody().getPosition().dst(getBody().getPosition()) <= EXAMINATION_DISTANCE) {
                nearestPOIInstance.setEnableFlicker(true);
            }

        }

    }

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
                if (verticalVelocity != -PLAYER_SPEED) {
                    verticalVelocity = PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.S:
            case Input.Keys.DOWN: {
                if (verticalVelocity != PLAYER_SPEED) {
                    verticalVelocity = -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.A:
            case Input.Keys.LEFT: {
                if (horizontalVelocity != PLAYER_SPEED) {
                    horizontalVelocity = -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.D:
            case Input.Keys.RIGHT: {
                if (horizontalVelocity != -PLAYER_SPEED) {
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
        }

        this.body.setLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
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

        this.body.setLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    public void stopAll(){
        this.currentBehavior = GameBehavior.IDLE;
        this.body.setLinearVelocity(0, 0);
    }


    public void setStartX(float startX) {
        this.startX = startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
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

            // Define Fixture
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1;
            fixtureDef.friction = 1;

            // Associate body to world
            body = world.createBody(bodyDef);
            body.setUserData(this);
            body.createFixture(fixtureDef);

            shape.dispose();
        }

    }
}
