package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.echoes.enums.EchoesActorType;

public class LoadingScreen implements Screen {

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private Texture loadScreen;

    public LoadingScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        loadScreen = assetManager.get("splash/loading_splash.png");
    }

    @Override
    public void show() {

        assetManager.load("sprites/walfrit_sheet.png", Texture.class);
        assetManager.load("sprites/walfrit_armored_sheet.png", Texture.class);
        assetManager.load("sprites/decorations_sheet.png", Texture.class);
        assetManager.load("sprites/poi_sheet.png", Texture.class);
        assetManager.load("sprites/shadow.png", Texture.class);
        assetManager.load("sprites/bounded_sheet.png", Texture.class);
        assetManager.load("sprites/strix_sheet.png", Texture.class);
        assetManager.load("sprites/hive_sheet.png", Texture.class);

        assetManager.load("sounds/SFX_collect&bonus13.wav", Sound.class);
        assetManager.load("sounds/SFX_hit&damage13.wav", Sound.class);;
        assetManager.load("sounds/SFX_hit&damage2.wav", Sound.class);
        assetManager.load("sounds/SFX_shot4.wav", Sound.class);
        assetManager.load("sounds/SFX_shot5.wav", Sound.class);
        assetManager.load("sounds/SFX_shot10.wav", Sound.class);
        assetManager.load("sounds/SFX_swordSwing.wav", Sound.class);
        assetManager.load("sounds/SFX_waterSplash.wav", Sound.class);
        assetManager.load("sounds/SFX_creatureDie4.wav", Sound.class);
        assetManager.load("sounds/SFX_hit&damage6.wav", Sound.class);

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
