package faust.lhipgame.instances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.PlayerEntity;
import faust.lhipgame.gameentities.enums.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Player Instance class
 */
public class PlayerInstance extends LivingInstance implements InputProcessor {

    private static final float PLAYER_SPEED = 100;
    private static final int EXAMINATION_DISTANCE = 50;

    private final List<GameInstance> roomPoiList = new ArrayList();

    public PlayerInstance() {
        super(new PlayerEntity());
        currentDirection = Direction.DOWN;

        Gdx.input.setInputProcessor((InputProcessor) this);
    }

    @Override
    public void logic() {

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
                if (!roomPoiList.isEmpty()) {
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

    private void examineNearestPOI() {
        POIInstance poiToExamine = (POIInstance) this.getNearestInstance(roomPoiList);
        if (poiToExamine.getBody().getPosition().dst(getBody().getPosition()) <= EXAMINATION_DISTANCE) {

            poiToExamine.examine();
        }
    }


    public void changePOIList(List<POIInstance> poiNewList) {
        Objects.nonNull(poiNewList);
        roomPoiList.clear();
        roomPoiList.addAll(poiNewList);
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
