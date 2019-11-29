package faust.lhipgame.instances;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.Player;

/**
 * Player Instance class
 */
public class PlayerInstance extends GameInstance implements InputProcessor {

    public PlayerInstance(GameEntity entity) {
        super(entity);
    }

    @Override
    public boolean keyDown(int keycode) {

        switch (keycode){
            case Input.Keys.UP:{
                this.bodyDef.linearVelocity.set(0,-1);
                break;
            }
            case Input.Keys.DOWN:{
                this.bodyDef.linearVelocity.set(0,1);
                break;
            }
            case Input.Keys.LEFT:{
                this.bodyDef.linearVelocity.set(-1,0);
                break;
            }
            case Input.Keys.RIGHT:{
                this.bodyDef.linearVelocity.set(1,0);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode){
            case Input.Keys.UP:
            case Input.Keys.DOWN:
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:{
                this.bodyDef.linearVelocity.set(0,0);
                break;
            }
        }
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
