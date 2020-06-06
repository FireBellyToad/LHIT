package faust.lhipgame.splash;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.splash.enums.SplashScreenEnum;
import faust.lhipgame.text.manager.TextManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Splash screen manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SplashManager {

    private final Map<SplashScreenEnum, GameEntity> splashScreens = new HashMap<>();
    private GameEntity splashToShow;
    private TextManager textManager;

    public SplashManager(TextManager textManager) {
        Objects.requireNonNull(textManager);

        this.textManager = textManager;

        Arrays.asList(SplashScreenEnum.values()).forEach((splashScreenEnum) -> {
            this.splashScreens.put(splashScreenEnum, new GameEntity(new Texture(splashScreenEnum.getSplashPath())) {
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

    public void setSplashToShow(SplashScreenEnum splashScreenEnum) {
        Objects.requireNonNull(splashScreenEnum);

        this.splashToShow = this.splashScreens.get(splashScreenEnum);
    }

    public void drawSplash(SpriteBatch batch) {
        Objects.requireNonNull(batch);

        batch.draw(splashToShow.getTexture(), 0, 0);

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
