package faust.lhipgame.instances;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.enums.Direction;

/**
 * Player Instance class
 */
public class PlayerInstance extends GameInstance implements InputProcessor {

    private static final float SPEED = 100;

    public PlayerInstance(GameEntity entity) {
        super(entity);
    }

    @Override
    public boolean keyDown(int keycode) {

        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        // Set instance direction and velocity accordingly to the pressed key
        // Check if not moving in opposite direction
        switch (keycode) {
            case Input.Keys.UP: {
                if (verticalVelocity != -SPEED) {
                    this.currentDirection = Direction.UP;
                    verticalVelocity = SPEED;
                }
                break;
            }
            case Input.Keys.DOWN: {
                if (verticalVelocity != SPEED) {
                    this.currentDirection = Direction.DOWN;
                    verticalVelocity = -SPEED;
                }
                break;
            }
            case Input.Keys.LEFT: {
                if (horizontalVelocity != SPEED) {
                    this.currentDirection = Direction.LEFT;
                    horizontalVelocity = -SPEED;
                }
                break;
            }
            case Input.Keys.RIGHT: {
                if (horizontalVelocity != -SPEED) {
                    this.currentDirection = Direction.RIGHT;
                    horizontalVelocity = SPEED;
                }
                break;
            }
        }

        this.body.setLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        // Determine new velocity
        switch (keycode) {
            case Input.Keys.UP:
            case Input.Keys.DOWN: {
                verticalVelocity = 0;
                break;
            }
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
