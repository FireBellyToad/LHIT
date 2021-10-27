package faust.lhitgame.game.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhitgame.LHITGame;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.gameentities.impl.TutorialEntity;
import faust.lhitgame.game.textbox.manager.TextBoxManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Splash screen manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SplashManager implements InputProcessor {

    private final Map<String, Texture> splashScreens = new HashMap<>();
    private String splashToShow;
    private final TextBoxManager textManager;
    private final TutorialEntity arrows;

    public SplashManager(TextBoxManager textManager, AssetManager assetManager) {
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(assetManager);

        this.textManager = textManager;
        this.arrows = new TutorialEntity(assetManager);

        //Extract all splash screens
        JsonValue splash = new JsonReader().parse(Gdx.files.internal("splash/splashScreen.json")).get("splashScreens");
        splash.forEach((s) -> this.splashScreens.put(s.getString("splashKey"), assetManager.get(s.getString("splashPath"))));
    }

    public void setSplashToShow(String splashScreen) {
        Objects.requireNonNull(splashScreen);

        this.splashToShow = splashScreen;

        //Check if valid splash screen
        if(!this.splashScreens.containsKey(splashToShow)){
            throw new RuntimeException("Invalid splashcreen " + splashScreen);
        }

        // Set this manager as input processor
        Gdx.input.setInputProcessor(this);
    }

    public void drawSplash(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        batch.begin();
        batch.draw(this.splashScreens.get(splashToShow), 0, 0);

        textManager.addNewTextBox(splashToShow);
        batch.draw( arrows.getFrame(GameBehavior.IDLE, DirectionEnum.DOWN,stateTime), LHITGame.GAME_WIDTH-24, LHITGame.GAME_HEIGHT -  16);
        batch.draw( arrows.getFrame(GameBehavior.IDLE, DirectionEnum.UP,stateTime), LHITGame.GAME_WIDTH-40, LHITGame.GAME_HEIGHT - 16);

        //TODO draw Tutorial Entity for K X
        batch.end();
    }

    /**
     * Closes splash
     */
    private void closeSplash() {
        Gdx.app.log("DEBUG", "END splash timer");
        splashToShow = null;
        textManager.removeAllBoxes();
    }

    /**
     *
     * @return true if splash is drawn
     */
    public boolean isDrawingSplash() {
        return !Objects.isNull(this.splashToShow);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.X || keycode == Input.Keys.K){
            closeSplash();
        }
        return false;
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
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
