package faust.lhipgame.game.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.game.gameentities.GameEntity;
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

    private final Map<String, GameEntity> splashScreens = new HashMap<>();
    private String splashToShow;
    private TextBoxManager textManager;
    private boolean isGameOverSplash;
    private Timer.Task splashTimer;

    public SplashManager(TextBoxManager textManager, AssetManager assetManager) {
        Objects.requireNonNull(textManager);

        this.textManager = textManager;
        this.isGameOverSplash = false;

        //Extract all splash screens
        JsonValue splash = new JsonReader().parse(Gdx.files.internal("splash/splashScreen.json")).get("splashScreens");
        splash.forEach((s) -> {
            this.splashScreens.put(s.getString("splashKey"),
                    new GameEntity(assetManager.get(s.getString("splashPath"))) {
                        @Override
                        protected int getTextureColumns() {
                            return 1;
                        }

                        @Override
                        protected int getTextureRows() {
                            return 1;
                        }
                    });
        });
    }

    public void setSplashToShow(String splashScreen) {
        Objects.requireNonNull(splashScreen);

        this.splashToShow = splashScreen;
        this.isGameOverSplash = (splashToShow == "splash.gameover");

        //Check if valid splash screen
        if(!this.splashScreens.containsKey(splashToShow)){
            throw new RuntimeException("Invalid splashcreen " + splashScreen);
        }
    }

    public void drawSplash(SpriteBatch batch) {
        Objects.requireNonNull(batch);

        batch.draw(this.splashScreens.get(splashToShow).getTexture(), 0, 0);

        if(!this.isGameOverSplash ){
            textManager.addNewTextBox(splashToShow);
        }

        //FIXME improve gameover logic
        // Hide splashToShow after time
        if(Objects.isNull(splashTimer)) {
            splashTimer = Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (isGameOverSplash) {
                        Gdx.app.exit();
                    }
                    Gdx.app.log("DEBUG", "END splash timer");
                    splashToShow = null;
                    textManager.removeAllBoxes();
                    splashTimer = null;
                }
            }, this.isGameOverSplash ? 3f : 1.5f);
            Gdx.app.log("DEBUG", "START splash timer" );
        }

    }

    public boolean isDrawingSplash() {
        return !Objects.isNull(this.splashToShow);
    }
}
