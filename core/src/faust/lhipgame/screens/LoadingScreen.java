package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.echoes.enums.EchoesActorType;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;

public class LoadingScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final Texture loadScreen;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x666666ff);

    private final ShapeRenderer cornerBox = new ShapeRenderer();
    private static final Color corner = new Color(0xffffffff);

    public LoadingScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        loadScreen = assetManager.get("splash/loading_splash.png");
    }

    @Override
    public void show() {

        assetManager.load("sprites/walfrit_sheet.png", Texture.class);
        assetManager.load("sprites/decorations_sheet.png", Texture.class);
        assetManager.load("sprites/poi_sheet.png", Texture.class);
        assetManager.load("sprites/shadow.png", Texture.class);
        assetManager.load("sprites/bounded_sheet.png", Texture.class);
        assetManager.load("sprites/strix_sheet.png", Texture.class);
        assetManager.load("sprites/hive_sheet.png", Texture.class);

        assetManager.load("sounds/SFX_collect&bonus13.ogg", Sound.class);
        assetManager.load("sounds/SFX_hit&damage13.ogg", Sound.class);
        assetManager.load("sounds/SFX_hit&damage2.ogg", Sound.class);
        assetManager.load("sounds/SFX_shot4.ogg", Sound.class);
        assetManager.load("sounds/SFX_shot5.ogg", Sound.class);
        assetManager.load("sounds/SFX_shot10.ogg", Sound.class);
        assetManager.load("sounds/SFX_swordSwing.ogg", Sound.class);
        assetManager.load("sounds/SFX_waterSplash.ogg", Sound.class);
        assetManager.load("sounds/SFX_creatureDie4.ogg", Sound.class);
        assetManager.load("sounds/SFX_hit&damage6.ogg", Sound.class);
        assetManager.load("sounds/horror_scream.ogg", Sound.class);

        musicManager.loadMusicFromFiles(assetManager);

        assetManager.load("splash/strix_splash.png", Texture.class);
        assetManager.load("splash/bounded_splash.png", Texture.class);
        assetManager.load("splash/morgengabe_splash.png", Texture.class);
        assetManager.load("splash/gameover_splash.png", Texture.class);
        assetManager.load("sprites/hud.png", Texture.class);
        assetManager.load("sprites/darkness_overlay.png", Texture.class);

        for(EchoesActorType echoActor : EchoesActorType.values()){
            assetManager.load(echoActor.getSpriteFilename(), Texture.class);
        }
    }

    @Override
    public void render(float delta) {

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

        assetManager.update();
        if(assetManager.isFinished()){
            game.setScreen(new GameScreen(game));
        }

        double loadingProgress = Math.floor(assetManager.getProgress()*100);

        //Load screen
        game.getBatch().begin();
        game.getBatch().draw(loadScreen,0,0);
        game.getBatch().end();

        //Black Corner
        game.getBatch().begin();
        backgroundBox.setColor(back);
        backgroundBox.setProjectionMatrix(cameraManager.getCamera().combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(10, LHIPGame.GAME_HEIGHT/2-6, LHIPGame.GAME_WIDTH-20,  10);
        backgroundBox.end();

        cornerBox.setColor(corner);
        cornerBox.setProjectionMatrix(cameraManager.getCamera().combined);
        cornerBox.begin(ShapeRenderer.ShapeType.Filled);
        cornerBox.rect(12, LHIPGame.GAME_HEIGHT/2-5, (float) (LHIPGame.GAME_WIDTH-25 - (100-loadingProgress)), 8);
        cornerBox.end();
        game.getBatch().end();

        Gdx.app.log("DEBUG", "Loading progress: " + loadingProgress + "%" );
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

    }
}
