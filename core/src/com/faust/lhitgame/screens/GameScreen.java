package com.faust.lhitgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.camera.CameraManager;
import com.faust.lhitgame.game.PauseManager;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.gameentities.enums.PlayerFlag;
import com.faust.lhitgame.game.hud.DarknessRenderer;
import com.faust.lhitgame.game.hud.Hud;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.rooms.manager.RoomsManager;
import com.faust.lhitgame.game.splash.SplashManager;
import com.faust.lhitgame.game.textbox.manager.TextBoxManager;
import com.faust.lhitgame.enums.cutscenes.CutsceneEnum;
import com.faust.lhitgame.utils.TextLocalizer;
import com.faust.lhitgame.game.world.manager.WorldManager;

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
    private final SplashManager splashManager;
    private final PauseManager pauseManager;

    private final Hud hud;
    private final DarknessRenderer darknessRenderer;

    private float stateTime = 0f;

    private final LHITGame game;
    private Timer.Task endGameTimer = null;

    public GameScreen(LHITGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.cameraManager = game.getCameraManager();
        this.musicManager = game.getMusicManager();
        this.textLocalizer = game.getTextLocalizer();
        textManager = new TextBoxManager(assetManager, textLocalizer);
        hud = new Hud(textManager, assetManager);
        splashManager = new SplashManager(textManager, assetManager);
        pauseManager = new PauseManager(game.getSaveFileManager(), game.getMusicManager(), assetManager);
        darknessRenderer = new DarknessRenderer(assetManager);
        worldManager = new WorldManager();
        player = new PlayerInstance(assetManager,this.game.isWebBuild());

    }

    @Override
    public void show() {
        Box2D.init();
        textLocalizer.loadTextFromLanguage();
        musicManager.initTuneMap(assetManager);
        Gdx.input.setInputProcessor(player);

        if(this.game.isWebBuild()){
            //Prevents arrow keys browser scrolling
            Gdx.input.setCatchKey(Input.Keys.UP, true);
            Gdx.input.setCatchKey(Input.Keys.DOWN, true);
            Gdx.input.setCatchKey(Input.Keys.LEFT, true);
            Gdx.input.setCatchKey(Input.Keys.RIGHT, true);
        }

        roomsManager = new RoomsManager(worldManager, textManager, splashManager, player, cameraManager.getCamera(),
                assetManager, game.getSaveFileManager(), game.getMusicManager());

    }

    @Override
    public void render(float delta) {

        //Render before game logic to avoid desync
        //Prevent animations while paused
        if (!pauseManager.isGamePaused()) {
            stateTime += Gdx.graphics.getDeltaTime();
        }

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


        // Stops game logic if splash screen is shown or game is paused
        if (!splashManager.isDrawingSplash() && !pauseManager.isGamePaused()) {

            // If player is not input processor, reset it
            if(!(Gdx.input.getInputProcessor() instanceof PlayerInstance))
                player.setAsInputProcessor();

            worldManager.doStep(delta);
            doLogic();
        } else if (pauseManager.isGamePaused()) {
            //Handle pause logic
            pauseManager.doLogic(game,player, roomsManager);
        }

    }

    private void drawOverlays() {
        //Draw overlay tiles
        roomsManager.drawCurrentRoomOverlays();

        // Draw splash XOR hud
        if (splashManager.isDrawingSplash()) {
            splashManager.drawSplash(game.getBatch(),stateTime);
        } else {
            darknessRenderer.drawDarkness(game.getBatch(), player.getBody().getPosition(), cameraManager.getCamera(), !GameBehavior.WALK.equals(player.getCurrentBehavior()));
            hud.drawHud(game.getBatch(), player, cameraManager.getCamera());
            if (pauseManager.isGamePaused()) {
                pauseManager.draw(game.getBatch(),cameraManager.getCamera());
            }
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

        //Pause
        if (player.getPlayerFlagValue(PlayerFlag.PAUSE_GAME)) {
            pauseManager.pauseGame();
            player.setPlayerFlagValue(PlayerFlag.PAUSE_GAME,false);
        }

        if (Objects.isNull(endGameTimer)) {
            if (player.getPlayerFlagValue(PlayerFlag.GO_TO_GAMEOVER)) {
                //Save game and go to game over
                endGameTimer =Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        player.setDamage(0);
                        roomsManager.dispose();
                        musicManager.stopMusic();
                        game.setScreen(new GameOverScreen(game));
                    }
                }, 3f);
            } else if (player.getPlayerFlagValue(PlayerFlag.PREPARE_END_GAME)) {
                endGameTimer =Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                game.setScreen(new CutsceneScreen(game, CutsceneEnum.ENDGAME));
                            }
                        }, 3f);
                Gdx.input.setInputProcessor(null);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.getViewport().update(width, height);
    }

    @Override
    public void pause() {
        player.setPlayerFlagValue(PlayerFlag.PAUSE_GAME, true);
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
