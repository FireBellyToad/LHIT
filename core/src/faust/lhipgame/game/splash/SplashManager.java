package faust.lhipgame.game.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.game.textbox.manager.TextBoxManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Splash screen manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SplashManager {

    private final Map<String, Texture> splashScreens = new HashMap<>();
    private String splashToShow;
    private final TextBoxManager textManager;
    private Timer.Task splashTimer;

    public SplashManager(TextBoxManager textManager, AssetManager assetManager) {
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(assetManager);

        this.textManager = textManager;

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
    }

    public void drawSplash(SpriteBatch batch) {
        Objects.requireNonNull(batch);
        batch.begin();
        batch.draw(this.splashScreens.get(splashToShow), 0, 0);

        textManager.addNewTextBox(splashToShow);

        // Hide splashToShow after time
        if(Objects.isNull(splashTimer)) {
            splashTimer = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.log("DEBUG", "END splash timer");
                    splashToShow = null;
                    textManager.removeAllBoxes();
                    splashTimer = null;
                }
            }, 2f);
            Gdx.app.log("DEBUG", "START splash timer" );
        }
        batch.end();
    }

    public boolean isDrawingSplash() {
        return !Objects.isNull(this.splashToShow);
    }
}
