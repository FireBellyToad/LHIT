package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.gameentities.PlayerEntity;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.world.WorldManager;

public class GameScreen implements Screen {


    private WorldManager worldManager;
    private PlayerInstance player;
    private OrthographicCamera camera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private float stateTime = 0f;
    private Viewport viewport;

    private final LHIPGame game;

    public GameScreen(LHIPGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        Box2D.init();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT);
        viewport = new FitViewport(LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT, camera);

        worldManager = new WorldManager();

        // Creating player and making it available to input processor
        player = new PlayerInstance(new PlayerEntity());

        box2DDebugRenderer = new Box2DDebugRenderer();

        worldManager.insertPlayerIntoWorld(player, LHIPGame.GAME_WIDTH / 2, LHIPGame.GAME_HEIGHT / 2);

    }

    @Override
    public void render(float delta) {

        worldManager.doStep();
        stateTime += Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        game.getBatch().begin();
        player.draw(game.getBatch(), stateTime);
        game.getBatch().end();

        box2DDebugRenderer.render(worldManager.getWorld(), camera.combined.scl(32f));

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        player.dispose();
        worldManager.dispose();
        box2DDebugRenderer.dispose();
    }
}
