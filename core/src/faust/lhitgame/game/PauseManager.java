package faust.lhitgame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import faust.lhitgame.LHITGame;
import faust.lhitgame.game.instances.impl.PlayerInstance;
import faust.lhitgame.game.music.MusicManager;
import faust.lhitgame.game.rooms.manager.RoomsManager;
import faust.lhitgame.menu.Menu;
import faust.lhitgame.menu.enums.MenuItem;
import faust.lhitgame.saves.RoomSaveEntry;
import faust.lhitgame.saves.SaveFileManager;
import faust.lhitgame.screens.MenuScreen;

import java.util.Map;

/**
 * @author Jacopo "Faust" Buttiglieri
 */
public class PauseManager {

    private final Menu menu;
    private final SaveFileManager saveFileManager;
    private boolean gamePaused = false;
    private final MusicManager musicManager;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x000000ff);


    public PauseManager(SaveFileManager saveFileManager, MusicManager musicManager, AssetManager assetManager) {

        this.musicManager = musicManager;
        this.saveFileManager = saveFileManager;
        menu = new Menu(saveFileManager, MenuItem.PAUSE_GAME, assetManager);
        menu.loadFonts(assetManager);
    }

    /**
     *
     * @param game
     * @param roomFlags
     */
    public void doLogic(LHITGame game, PlayerInstance playerInstance, RoomsManager roomsManager){
        //Exit or resume game game
        if(menu.isChangeToGameScreen()){
            resumeGame();
        } else if (menu.isChangeToNextScreen()){
            saveFileManager.saveOnFile(playerInstance,roomsManager.getSaveMap());
            game.setScreen(new MenuScreen(game));
        }
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {

        //Black Background
        batch.begin();
        backgroundBox.setColor(back);
        backgroundBox.setProjectionMatrix(camera.combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, (float) ((LHITGame.GAME_HEIGHT * 0.5)-40), LHITGame.GAME_WIDTH , 30);
        backgroundBox.end();
        batch.end();

        batch.begin();
        menu.drawCurrentMenu(batch);
        batch.end();
    }

    /**
     * Pause and set as inputProcessor
     */
    public void pauseGame(){
        gamePaused = true;
        musicManager.pauseMusic();
        Gdx.input.setInputProcessor(menu);
    }

    /**
     * Unpause and reset menu
     */
    public void resumeGame(){
        gamePaused = false;
        musicManager.resumeMusic();
        menu.reset();
    }

    public boolean isGamePaused() {
        return gamePaused;
    }
}
