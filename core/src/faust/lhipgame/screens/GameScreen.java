package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.instances.POIInstance;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private WorldManager worldManager;
    private PlayerInstance player;
    private TextManager textManager;

    private OrthographicCamera camera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private float stateTime = 0f;
    private Viewport viewport;
    private ShapeRenderer background;

    private final LHIPGame game;
    private List<POIInstance> poiList;

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
        textManager = new TextManager();

        // Creating player and making it available to input processor
        player = new PlayerInstance();

        poiList = new ArrayList<POIInstance>() {{
            this.add(new POIInstance(textManager));
        }};

        player.changePOIList(poiList);

        box2DDebugRenderer = new Box2DDebugRenderer();

        worldManager.insertPlayerIntoWorld(player, LHIPGame.GAME_WIDTH / 2, LHIPGame.GAME_HEIGHT / 2);
        worldManager.insertPOIIntoRoom(poiList);

        background = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {

        doLogic();
        worldManager.doStep();
        stateTime += Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        game.getBatch().setProjectionMatrix(camera.combined);

        //Draw gray background
        drawBackGround();

        //Draw all instances
        drawGameInstances();

        //Draw all overlays
        drawOverlays();

        box2DDebugRenderer.render(worldManager.getWorld(), camera.combined.scl(32f));

    }

    private void drawOverlays() {
        game.getBatch().begin();
        textManager.renderTextBoxes(game.getBatch());
        game.getBatch().end();
    }

    private void drawGameInstances() {
        game.getBatch().begin();

        poiList.forEach((poi) -> poi.draw(game.getBatch(),stateTime));

        player.draw(game.getBatch(), stateTime);
        game.getBatch().end();
    }

    /**
     * Draws the background
     */
    private void drawBackGround() {
        game.getBatch().begin();
        background.setColor(Color.GRAY);
        background.setProjectionMatrix(camera.combined);
        background.begin(ShapeRenderer.ShapeType.Filled);
        background.rect(0, 0, LHIPGame.GAME_WIDTH, LHIPGame.GAME_HEIGHT);
        background.end();
        game.getBatch().end();
    }

    /**
     * Executes the logic of each game Instance
     */
    private void doLogic() {

        player.logic();

        // for each enemies -> logic()
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
        textManager.dispose();
        box2DDebugRenderer.dispose();
    }
}
