package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.echoes.enums.EchoesActorType;

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
        assetManager.load("sprites/decorations_sheet.png", Texture.class);
        assetManager.load("sprites/poi_sheet.png", Texture.class);
        assetManager.load("sprites/shadow.png", Texture.class);
        assetManager.load("sprites/bounded_sheet.png", Texture.class);
        assetManager.load("sprites/strix_sheet.png", Texture.class);

        assetManager.load("splash/strix_splash.png", Texture.class);
        assetManager.load("splash/morgengabe_splash.png", Texture.class);
        assetManager.load("splash/gameover_splash.png", Texture.class);
        assetManager.load("sprites/hud.png", Texture.class);

        assetManager.load("fonts/main_font.fnt", BitmapFont.class);
        assetManager.load("fonts/main_font.png", Texture.class);

        for(EchoesActorType echoActor : EchoesActorType.values()){
            assetManager.load(echoActor.getSpriteFilename(), Texture.class);
        }
    }

    @Override
    public void render(float delta) {

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);
        cameraManager.renderBackground();

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
