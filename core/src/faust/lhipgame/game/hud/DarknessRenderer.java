package faust.lhipgame.game.hud;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.instances.impl.PlayerInstance;

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

    public void drawDarkness(SpriteBatch batch, PlayerInstance player, OrthographicCamera camera)  {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(player);
        Objects.requireNonNull(camera);

        final float xOffset = Math.max(0, player.getBody().getPosition().x - LHIPGame.GAME_WIDTH/2);

        //Left overflow
        batch.begin();
        backgroundBox.setColor(darkness);
        backgroundBox.setProjectionMatrix(camera.combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, 0, Math.max(0,Math.min(16,0 + xOffset)),  LHIPGame.GAME_HEIGHT-12);
        backgroundBox.end();
        batch.end();

        //Right overflow
        batch.begin();
        backgroundBox.setColor(darkness);
        backgroundBox.setProjectionMatrix(camera.combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(xOffset+144, 0, 16-xOffset,  LHIPGame.GAME_HEIGHT-12);
        backgroundBox.end();
        batch.end();

        //Darkness
        batch.begin();
        batch.draw(darknessOverlay,Math.min(16,0 + xOffset),0);
        batch.end();
    }
}
