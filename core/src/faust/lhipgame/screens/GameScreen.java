package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.hud.Hud;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.rooms.manager.RoomsManager;
import faust.lhipgame.splash.SplashManager;
import faust.lhipgame.text.manager.TextManager;
import faust.lhipgame.world.manager.WorldManager;

public class GameScreen implements Screen {

    public static final long FLICKER_DURATION_IN_NANO = 125000000; // 1/8 second in nanoseconds

    private AssetManager assetManager;
    private CameraManager cameraManager;
    private WorldManager worldManager;
    private PlayerInstance player;
    private TextManager textManager;
    private RoomsManager roomsManager;

    private Hud hud;
    private SplashManager splashManager;

    private float stateTime = 0f;

    private final LHIPGame game;

    public GameScreen(LHIPGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.cameraManager = game.getCameraManager();
    }

    @Override
    public void show() {
        Box2D.init();

        worldManager = new WorldManager(assetManager);
        textManager = new TextManager(assetManager);
        hud = new Hud(textManager,assetManager);
        splashManager = new SplashManager(textManager,assetManager);

        // Creating player and making it available to input processor
        player = new PlayerInstance(assetManager);

        roomsManager = new RoomsManager(worldManager, textManager, splashManager, player, cameraManager.getCamera(), assetManager);
    }

    @Override
    public void render(float delta) {

        // Stops game logic if splash screen is shown
        if (!splashManager.isDrawingSplash()) {
            doLogic();
            worldManager.doStep();
        }

        stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

        if (!splashManager.isDrawingSplash()) {
            //Draw gray background
            drawBackGround();

            //Draw Room and all contents
            drawRoomAndContents(stateTime);
        }

        //Draw all overlays
        drawOverlays();

        //cameraManager.box2DDebugRenderer(worldManager.getWorld());

    }

    private void drawOverlays() {
        // Draw splash XOR hud
        if (splashManager.isDrawingSplash()) {
            game.getBatch().begin();
            splashManager.drawSplash(game.getBatch());
            game.getBatch().end();
        } else {
            hud.drawHud(game.getBatch(), player, cameraManager.getCamera());
        }
        // draw text
        textManager.renderTextBoxes(game.getBatch(), player, cameraManager.getCamera(), splashManager.isDrawingSplash());
    }

    private void drawRoomAndContents(float stateTime) {
        game.getBatch().begin();
        roomsManager.drawCurrentRoomContents(game.getBatch(), stateTime);
        game.getBatch().end();
    }

    /**
     * Draws the background
     */
    private void drawBackGround() {
        game.getBatch().begin();
        cameraManager.renderBackground();
        roomsManager.drawCurrentRoomBackground();
        game.getBatch().end();
    }

    /**
     * Executes the logic of each game Instance
     */
    private void doLogic() {

        roomsManager.doRoomContentsLogic(stateTime);
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.getViewport().update(width, height);
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
        roomsManager.dispose();
        worldManager.dispose();
        textManager.dispose();
        assetManager.dispose();
    }
}
