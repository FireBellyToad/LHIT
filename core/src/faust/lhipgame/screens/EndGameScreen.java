package faust.lhipgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.gameentities.GameEntity;
import faust.lhipgame.game.gameentities.SpriteEntity;
import faust.lhipgame.game.hud.enums.HudIconsEnum;
import faust.lhipgame.game.music.MusicManager;
import faust.lhipgame.game.music.enums.TuneEnum;
import faust.lhipgame.game.utils.TextLocalizer;
import faust.lhipgame.menu.Menu;
import faust.lhipgame.menu.enums.MenuItem;
import faust.lhipgame.saves.SaveFileManager;

import java.util.Map;

public class EndGameScreen implements Screen {

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private static final float X_OFFSET = 50;
    private static final float Y_OFFSET = (float) (LHIPGame.GAME_HEIGHT * 0.66);

    private final LHIPGame game;
    private final AssetManager assetManager;
    private final CameraManager cameraManager;
    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final SaveFileManager saveFileManager;
    private final Menu menu;
    private final SpriteEntity itemsTexture;
    private final Map<String, Object> valuesMap;


    public EndGameScreen(LHIPGame game) {
        this.game = game;
        assetManager = game.getAssetManager();
        cameraManager = game.getCameraManager();
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();
        saveFileManager = game.getSaveFileManager();
        valuesMap = saveFileManager.loadRawValues();

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


        menu = new Menu(game.getSaveFileManager(), MenuItem.END_GAME);
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

        batch.begin();
        //Morgengabes found count. Set in red if all has been found
        batch.draw(itemsTexture.getFrame(HudIconsEnum.MORGENGABE.ordinal() * GameEntity.FRAME_DURATION),
                X_OFFSET,
                Y_OFFSET);

        menu.getMainFont().draw(batch,
                " : " + valuesMap.get("morgengabes") + " " + textLocalizer.localizeFromKey("boxes","endgame.of") + " 9",
                X_OFFSET + 10,
                Y_OFFSET + 6);
        batch.end();

    }

    /**
     *
     * @param batch
     */
    private void drawBlackBackground(SpriteBatch batch) {
        batch.begin();
        backgroundBox.setColor(darkness);
        backgroundBox.setProjectionMatrix(cameraManager.getCamera().combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, 0, LHIPGame.GAME_WIDTH,  LHIPGame.GAME_HEIGHT);
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