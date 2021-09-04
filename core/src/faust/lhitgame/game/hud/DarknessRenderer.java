package faust.lhitgame.game.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhitgame.LHITGame;
import faust.lhitgame.game.instances.impl.PlayerInstance;

import java.util.Objects;

/**
 * Darkness renderer class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class DarknessRenderer {

    private final Texture darknessOverlay;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    public DarknessRenderer(AssetManager assetManager) {
        Objects.requireNonNull(assetManager);

        darknessOverlay = assetManager.get("sprites/darkness_overlay.png");

    }

    public void drawDarkness(SpriteBatch batch, PlayerInstance player, OrthographicCamera camera) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(player);
        Objects.requireNonNull(camera);

        final float xOffset = player.getBody().getPosition().x + 6 - LHITGame.GAME_WIDTH / 2;
        final float yOffset = player.getBody().getPosition().y + 8 - LHITGame.GAME_HEIGHT / 2;

        //Left overflow
        if (0 + xOffset > 0) {
            batch.begin();
            backgroundBox.setColor(darkness);
            backgroundBox.setProjectionMatrix(camera.combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(0, 0, 0 + xOffset, LHITGame.GAME_HEIGHT - 12);
            backgroundBox.end();
            batch.end();
        }

        //Right overflow
        if (16 - xOffset > 0) {
            batch.begin();
            backgroundBox.setColor(darkness);
            backgroundBox.setProjectionMatrix(camera.combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(xOffset + 144, 0, 16 - xOffset, LHITGame.GAME_HEIGHT - 12);
            backgroundBox.end();
            batch.end();
        }

        //Up overflow
        if (yOffset < 1) {
            batch.begin();
            backgroundBox.setColor(darkness);
            backgroundBox.setProjectionMatrix(camera.combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(0 + xOffset, LHITGame.GAME_HEIGHT - 12, LHITGame.GAME_WIDTH + (16 - xOffset), yOffset);
            backgroundBox.end();
            batch.end();
        }

        //Down overflow
        if (yOffset > 0) {
            batch.begin();
            backgroundBox.setColor(darkness);
            backgroundBox.setProjectionMatrix(camera.combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(0 + xOffset, 0, LHITGame.GAME_WIDTH + (16 - xOffset), yOffset);
            backgroundBox.end();
            batch.end();
        }

        //Darkness
        batch.begin();
        batch.draw(darknessOverlay, 0 + xOffset, 0 + yOffset);
        batch.end();
    }
}
