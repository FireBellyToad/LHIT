package com.faust.lhitgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.camera.CameraManager;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.music.enums.TuneEnum;
import com.faust.lhitgame.enums.cutscenes.CutsceneEnum;
import com.faust.lhitgame.utils.TextLocalizer;
import com.faust.lhitgame.menu.Menu;

/**
 * Menu screen class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MenuScreen implements Screen {

    private final LHITGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;
    private final Texture titleTexture;

    public MenuScreen(LHITGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();

        titleTexture = assetManager.get("splash/title_splash.png");
        musicManager.loadSingleTune(TuneEnum.TITLE, assetManager);

        menu = new Menu(game.getSaveFileManager(),assetManager);
    }

    @Override
    public void show() {
        menu.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        //Loop title music
        musicManager.playMusic(TuneEnum.TITLE);

        Gdx.input.setInputProcessor(menu);

        if(this.game.isWebBuild()){
            //Prevents arrow keys browser scrolling
            Gdx.input.setCatchKey(Input.Keys.UP, true);
            Gdx.input.setCatchKey(Input.Keys.DOWN, true);
            Gdx.input.setCatchKey(Input.Keys.LEFT, true);
            Gdx.input.setCatchKey(Input.Keys.RIGHT, true);
        }
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToIntroScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new CutsceneScreen(game, CutsceneEnum.INTRO));
        } else if (menu.isChangeToGameScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new GameScreen(game));
        }  else if (menu.isChangeToCreditScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new CutsceneScreen(game, CutsceneEnum.CREDITS));
        }  else if (menu.isChangeToStoryScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new CutsceneScreen(game, CutsceneEnum.STORY));
        }else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(titleTexture, 0, 0);
            menu.drawCurrentMenuLocalized(game.getBatch(), textLocalizer);
            game.getBatch().end();
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

    }
}