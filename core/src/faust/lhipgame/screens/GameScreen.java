package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Timer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.hud.DarknessRenderer;
import faust.lhipgame.game.hud.Hud;
import faust.lhipgame.game.instances.impl.PlayerInstance;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.rooms.manager.RoomsManager;
import faust.lhipgame.game.splash.SplashManager;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.utils.CutsceneEnum;
import faust.lhipgame.utils.TextLocalizer;
import faust.lhipgame.game.world.manager.WorldManager;

import java.util.Objects;

public class GameScreen implements Screen {

    public static final long FLICKER_DURATION_IN_NANO = 125000000; // 1/8 second in nanoseconds

    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final WorldManager worldManager;
    private final PlayerInstance player;
    private final TextBoxManager textManager;
    private RoomsManager roomsManager;

    private final Hud hud;
    private final DarknessRenderer darknessRenderer;
    private final SplashManager splashManager;

    private float stateTime = 0f;

    private final LHIPGame game;
    private Timer.Task endGameTimer = null;

    public GameScreen(LHIPGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.cameraManager = game.getCameraManager();
        this.musicManager = game.getMusicManager();
        this.textLocalizer = game.getTextLocalizer();
        textManager = new TextBoxManager(assetManager,textLocalizer);
        hud = new Hud(textManager,assetManager);
        splashManager = new SplashManager(textManager,assetManager);
        darknessRenderer = new DarknessRenderer(assetManager);
        worldManager = new WorldManager();
        player = new PlayerInstance(assetManager);


    }

    @Override
    public void show() {
        Box2D.init();
        textLocalizer.loadTextFromLanguage();
        musicManager.initTuneMap(assetManager);
        Gdx.input.setInputProcessor(player);

        roomsManager = new RoomsManager(worldManager, textManager, splashManager, player, cameraManager.getCamera(),
                assetManager, game.getSaveFileManager(), game.getMusicManager());

    }

    @Override
    public void render(float delta) {

        // Stops game logic if splash screen is shown
        if (!splashManager.isDrawingSplash()) {
            worldManager.doStep();
            doLogic();
        }

        stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

        if (!splashManager.isDrawingSplash()) {
            //Draw gray background
            drawBackground();

            //Draw Room and all contents
            drawRoomAndContents(stateTime);
        }

        //Draw all overlays
        drawOverlays();

//       cameraManager.box2DDebugRenderer(worldManager.getWorld());

    }

    private void drawOverlays() {
        //Draw overlay tiles
        roomsManager.drawCurrentRoomOverlays();

        // Draw splash XOR hud
        if (splashManager.isDrawingSplash()) {
            splashManager.drawSplash(game.getBatch());
        } else {
            darknessRenderer.drawDarkness(game.getBatch(), player, cameraManager.getCamera());
            hud.drawHud(game.getBatch(), player, cameraManager.getCamera());
        }
        // draw text
        textManager.renderTextBoxes(game.getBatch(), cameraManager.getCamera());
    }

    private void drawRoomAndContents(float stateTime) {
        roomsManager.drawCurrentRoomContents(game.getBatch(), stateTime);
    }

    /**
     * Draws the background color and terrain tiles
     */
    private void drawBackground() {
        game.getBatch().begin();
        cameraManager.renderBackground();
        game.getBatch().end();
        roomsManager.drawCurrentRoomBackground();
    }

    /**
     * Executes the logic of each game Instance
     */
    private void doLogic() {

        roomsManager.doRoomContentsLogic(stateTime);

        if(player.isDead()){
            //Save game and go to game over
            player.setDamage(0);
            roomsManager.dispose();
            musicManager.stopMusic();
            game.setScreen(new GameOverScreen(game));
        } else if (player.isPrepareEndgame() && Objects.isNull(endGameTimer)){
            endGameTimer =
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            game.setScreen( new CutsceneScreen(game, CutsceneEnum.ENDGAME));
                        }
                    }, 3f);
            Gdx.input.setInputProcessor(null);
        }
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
