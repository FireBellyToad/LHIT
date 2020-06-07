package faust.lhipgame.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.text.manager.TextManager;

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
    private TextManager textManager;

    public SplashManager(TextManager textManager) {
        Objects.requireNonNull(textManager);

        this.textManager = textManager;

        JsonValue splash = new JsonReader().parse(Gdx.files.internal("splash/splashScreen.json")).get("splashScreens");
        splash.forEach((s) -> {
            this.splashScreens.put(s.getString("splashKey"),
                    new GameEntity(new Texture(s.getString("splashPath"))) {
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
    }

    public void drawSplash(SpriteBatch batch) {
        Objects.requireNonNull(batch);

        batch.draw(this.splashScreens.get(splashToShow).getTexture(), 0, 0);
        textManager.addNewTextBox(splashToShow);

        // Hide splashToShow after time
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                splashToShow = null;
            }
        }, 0.5f);

    }

    public boolean isDrawingSplash() {
        return !Objects.isNull(this.splashToShow);
    }
}
