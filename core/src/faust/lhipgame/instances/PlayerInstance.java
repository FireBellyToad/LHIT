package faust.lhipgame.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import faust.lhipgame.gameentities.PlayerEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.text.TextManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player Instance class
 */
public class PlayerInstance extends LivingInstance implements InputProcessor {

    private static final float PLAYER_SPEED = 100;
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
                    this.currentDirection = Direction.UP;
                    verticalVelocity = PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.S:
            case Input.Keys.DOWN: {
                if (verticalVelocity != PLAYER_SPEED) {
                    this.currentDirection = Direction.DOWN;
                    verticalVelocity = -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.A:
            case Input.Keys.LEFT: {
                if (horizontalVelocity != PLAYER_SPEED) {
                    this.currentDirection = Direction.LEFT;
                    horizontalVelocity = -PLAYER_SPEED;
                }
                break;
            }
            case Input.Keys.D:
            case Input.Keys.RIGHT: {
                if (horizontalVelocity != -PLAYER_SPEED) {
                    this.currentDirection = Direction.RIGHT;
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
