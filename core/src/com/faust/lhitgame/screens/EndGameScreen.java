package com.faust.lhitgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.camera.CameraManager;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.SpriteEntity;
import com.faust.lhitgame.game.hud.enums.HudIconsEnum;
import com.faust.lhitgame.game.music.MusicManager;
import com.faust.lhitgame.game.music.enums.TuneEnum;
import com.faust.lhitgame.menu.Menu;
import com.faust.lhitgame.menu.enums.MenuItem;
import com.faust.lhitgame.saves.enums.SaveFieldsEnum;
import com.faust.lhitgame.utils.TextLocalizer;

import java.util.Map;
import java.util.Objects;

public class EndGameScreen implements Screen {

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private static final float X_OFFSET = 50;
    private static final float Y_OFFSET = (float) (LHITGame.GAME_HEIGHT * 0.66);

    private final LHITGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;
    private final SpriteEntity itemsTexture;
    private final Map<String, Object> valuesMap;


    public EndGameScreen(LHITGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();
        valuesMap = game.getSaveFileManager().loadRawValues();

        this.itemsTexture = new SpriteEntity(assetManager.get("sprites/hud.png")) {
            @Override
            protected int getTextureColumns() {
                return HudIconsEnum.values().length;
            }

            @Override
            protected int getTextureRows() {
                return 1;
            }
        };


        menu = new Menu(game.getSaveFileManager(), MenuItem.END_GAME, assetManager);
    }

    @Override
    public void show() {
        menu.loadFonts(assetManager);

        //Loop title music
        musicManager.playMusic(TuneEnum.ENDGAME, false);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToNextScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new MenuScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Black background
            drawBlackBackground(game.getBatch());

            //Draw items
            drawItems(game.getBatch());

            //Menu screen render
            game.getBatch().begin();
            menu.drawCurrentMenuLocalized(game.getBatch(), textLocalizer);
            game.getBatch().end();
        }

    }

    private void drawItems(SpriteBatch batch) {
        Objects.requireNonNull(valuesMap);

        batch.begin();
        //The end
        menu.getMainFont().draw(batch,
                textLocalizer.localizeFromKey("boxes", "endgame.end"),
                X_OFFSET,
                Y_OFFSET + 30);

        //crosses found count.
        batch.draw(itemsTexture.getFrame(HudIconsEnum.GOLDCROSS.ordinal() * AnimatedEntity.FRAME_DURATION),
                X_OFFSET - 10,
                Y_OFFSET + 6);

        menu.getMainFont().draw(batch,
                " : " + valuesMap.get(SaveFieldsEnum.CROSSES.getFieldName()) + " " + textLocalizer.localizeFromKey("boxes", "endgame.of") + " 9",
                X_OFFSET,
                Y_OFFSET + 12);

        //Herb found count.
        batch.draw(itemsTexture.getFrame(HudIconsEnum.HERBS.ordinal() * AnimatedEntity.FRAME_DURATION),
                X_OFFSET - 10,
                Y_OFFSET - 6);

        menu.getMainFont().draw(batch,
                " : " + valuesMap.get(SaveFieldsEnum.HERBS_FOUND.getFieldName()) + " " + textLocalizer.localizeFromKey("boxes", "endgame.of") + " 3",
                X_OFFSET,
                Y_OFFSET);

        //Armor found.
        batch.draw(itemsTexture.getFrame(HudIconsEnum.ARMOR.ordinal() * AnimatedEntity.FRAME_DURATION),
                X_OFFSET - 10,
                Y_OFFSET - 18);

        menu.getMainFont().draw(batch,
                " : " + ((boolean) valuesMap.get(SaveFieldsEnum.ARMOR.getFieldName()) ? 1 : 0) + " " + textLocalizer.localizeFromKey("boxes", "endgame.of") + " 1",
                X_OFFSET,
                Y_OFFSET - 12);

        //Secret boss killed.
        batch.draw(itemsTexture.getFrame(HudIconsEnum.ARMOR.ordinal() * AnimatedEntity.FRAME_DURATION),
                X_OFFSET - 10,
                Y_OFFSET - 30);

        menu.getMainFont().draw(batch,
                " : " + ((boolean) valuesMap.get(SaveFieldsEnum.KILLED_SECRET.getFieldName()) ? 1 : 0) + " " + textLocalizer.localizeFromKey("boxes", "endgame.of") + " 1",
                X_OFFSET,
                Y_OFFSET - 24);
        batch.end();

    }


    /**
     * @param batch
     */
    private void drawBlackBackground(SpriteBatch batch) {
        batch.begin();
        backgroundBox.setColor(darkness);
        backgroundBox.setProjectionMatrix(cameraManager.getCamera().combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, 0, LHITGame.GAME_WIDTH, LHITGame.GAME_HEIGHT);
        backgroundBox.end();
        batch.end();
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