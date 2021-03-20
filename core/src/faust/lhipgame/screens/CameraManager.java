package faust.lhipgame.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import faust.lhipgame.LHIPGame;

import java.util.Objects;

/**
 * Camera manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CameraManager {

    private OrthographicCamera camera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Viewport viewport;

    private ShapeRenderer background;
    private static final Color back = new Color(0x595959ff);

    public CameraManager() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT);
        viewport = new FillViewport(LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT, camera);
        box2DDebugRenderer = new Box2DDebugRenderer();
        background = new ShapeRenderer();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void box2DDebugRenderer(World world) {
        Objects.requireNonNull(world);
        box2DDebugRenderer.render(world, camera.combined);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void applyAndUpdate() {

        viewport.apply();
        camera.update();
    }

    public void renderBackground() {
        background.setColor(back);
        background.setProjectionMatrix(camera.combined);
        background.begin(ShapeRenderer.ShapeType.Filled);
        background.rect(0, 0, LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT);
        background.end();
    }

    public void dispose() {
        box2DDebugRenderer.dispose();
    }
}
