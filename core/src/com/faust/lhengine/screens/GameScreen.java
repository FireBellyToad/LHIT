package com.faust.lhengine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Timer;
import com.faust.lhengine.LHEngine;
import com.faust.lhengine.camera.CameraManager;
import com.faust.lhengine.game.PauseManager;
import com.faust.lhengine.game.gameentities.enums.PlayerFlag;
import com.faust.lhengine.game.hud.DarknessRenderer;
import com.faust.lhengine.game.hud.Hud;
import com.faust.lhengine.game.instances.impl.PlayerInstance;
import com.faust.lhengine.game.music.MusicManager;
import com.faust.lhengine.game.renderer.TopViewWorldRenderer;
import com.faust.lhengine.game.rooms.manager.RoomsManager;
import com.faust.lhengine.game.splash.SplashManager;
import com.faust.lhengine.game.textbox.manager.TextBoxManager;
import com.faust.lhengine.enums.cutscenes.CutsceneEnum;
import com.faust.lhengine.utils.TextLocalizer;
import com.faust.lhengine.game.world.manager.WorldManager;

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
    private final SplashManager splashManager;
    private final PauseManager pauseManager;
    private TopViewWorldRenderer worldRenderer;
    private RoomsManager roomsManager;

    private final Hud hud;

    private float stateTime = 0f;

    private final LHEngine game;
    private Timer.Task endGameTimer = null;

    public GameScreen(LHEngine game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.cameraManager = game.getCameraManager();
        this.musicManager = game.getMusicManager();
        this.textLocalizer = game.getTextLocalizer();
        textManager = new TextBoxManager(assetManager, textLocalizer);
        hud = new Hud(textManager, assetManager);
        splashManager = new SplashManager(textManager, assetManager);
        pauseManager = new PauseManager(game.getSaveFileManager(), musicManager, assetManager);
        worldManager = new WorldManager(this.game.isWebBuild());
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
        worldRenderer = new TopViewWorldRenderer(game.getBatch(), cameraManager, splashManager, new DarknessRenderer(assetManager));


    }

    @Override
    public void render(float delta) {

        // Stops game logic if splash screen is shown or game is paused
        if (!splashManager.isDrawingSplash() && !pauseManager.isGamePaused()) {

            // If player is not input processor, reset it
            if(!(Gdx.input.getInputProcessor() instanceof PlayerInstance))
                player.setAsInputProcessor();

            worldManager.doStep();
            doLogic();
        } else if (pauseManager.isGamePaused()) {
            //Handle pause logic
            pauseManager.doLogic(game,player, roomsManager);
        }

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
            worldRenderer.drawBackground(roomsManager.getCurrentRoom());

            //Draw Room and all contents
            worldRenderer.drawRoomAndContents(stateTime, roomsManager.getCurrentRoom());
        }

        //Draw all overlays
        worldRenderer.drawOverlays(stateTime,hud, player,roomsManager.getCurrentRoom(), pauseManager, textManager);

//       cameraManager.box2DDebugRenderer(worldManager.getWorld());

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
