package faust.lhipgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import faust.lhipgame.gameentities.PlayerEntity;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.world.WorldManager;

public class LHIPGame extends Game {

	private static final int GAME_WIDTH = 160;
	private static final int GAME_HEIGHT = 144;

    private SpriteBatch batch;
    private WorldManager worldManager;
    private PlayerInstance player;
    private OrthographicCamera camera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private float stateTime = 0f;
    private Viewport viewport;

    @Override
    public void create() {
        Box2D.init();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        viewport = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);

        worldManager = new WorldManager();

        // Creating player and making it available to input processor
        player = new PlayerInstance(new PlayerEntity());

        batch = new SpriteBatch();
        box2DDebugRenderer = new Box2DDebugRenderer();

        worldManager.insertPlayerIntoWorld(player, GAME_WIDTH/2, GAME_HEIGHT/2);
    }

    @Override
    public void render() {
        worldManager.doStep();
        stateTime += Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.draw(batch, stateTime);
        batch.end();

        box2DDebugRenderer.render(worldManager.getWorld(), camera.combined.scl(32f));
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        worldManager.dispose();
        box2DDebugRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
