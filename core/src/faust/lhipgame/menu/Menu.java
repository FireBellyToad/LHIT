package faust.lhipgame.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import faust.lhipgame.LHIPGame;
import faust.lhipgame.game.textbox.interfaces.TextLocalizer;
import faust.lhipgame.menu.enums.MenuItem;
import faust.lhipgame.saves.SaveFileManager;

import java.util.Objects;

/**
 * Menu class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Menu implements InputProcessor {

    private static final float FONT_SIZE = 0.5f;
    private static final float MENU_X_OFFSET = 50;
    private static final float MENU_Y_OFFSET = (float) (LHIPGame.GAME_HEIGHT * 0.5);
    private static final float SPAN = 15;
    private static final String ARROW_CHARACTER = "~";

    private BitmapFont mainFont;

    private MenuItem currentMenu = MenuItem.MAIN;
    private int selectedMenuVoice = 0;
    private boolean changeToGameScreen = false;
    private boolean changeToNextScreen = false;
    private boolean changeToIntroScreen = false;

    private final SaveFileManager saveFileManager;

    public Menu(SaveFileManager saveFileManager) {
        this.saveFileManager = saveFileManager;

    }

    public Menu(SaveFileManager saveFileManager, MenuItem currentMenu) {
        this.saveFileManager = saveFileManager;
        this.currentMenu = currentMenu;
    }

    public void loadFonts(AssetManager assetManager) {

        // Prepare font
        mainFont = assetManager.get("fonts/main_font.fnt");
        mainFont.getData().setScale(FONT_SIZE);
    }

    /**
     * Draw current Menu with localized voices
     *
     * @param batch
     * @param textBoxManager
     */
    public void drawCurrentMenuLocalized(SpriteBatch batch, TextLocalizer textBoxManager) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(currentMenu);
        Objects.requireNonNull(currentMenu.getSubItems()); // Must have subitems!

        //Title
        if (Objects.nonNull(currentMenu.getTitleMessageKey()))
            mainFont.draw(batch, textBoxManager.localizeFromKey(currentMenu.getTitleMessageKey()), MENU_X_OFFSET, MENU_Y_OFFSET);

        MenuItem[] subItemsArray = currentMenu.getSubItems();
        for (int i = 0; i < subItemsArray.length; i++) {
            mainFont.draw(batch, textBoxManager.localizeFromKey(subItemsArray[i].name()),
                    MENU_X_OFFSET, MENU_Y_OFFSET - (SPAN * (1 + i)));
        }
        //Draw arrow on selected option
        mainFont.draw(batch, ARROW_CHARACTER, MENU_X_OFFSET - 10, MENU_Y_OFFSET - (SPAN * (1 + this.selectedMenuVoice)));
    }

    /**
     * Draw current menu without localization
     * @param batch
     */
    public void drawCurrentMenu(SpriteBatch batch) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(currentMenu);
        Objects.requireNonNull(currentMenu.getSubItems()); // Must have subitems!

        MenuItem[] subItemsArray = currentMenu.getSubItems();
        for (int i = 0; i < subItemsArray.length; i++) {
            mainFont.draw(batch, subItemsArray[i].name().replace('_',' '),
                    MENU_X_OFFSET, MENU_Y_OFFSET - (SPAN * (1 + i)));
        }
        //Draw arrow on selected option
        mainFont.draw(batch, ARROW_CHARACTER, MENU_X_OFFSET - 10, MENU_Y_OFFSET - (SPAN * (1 + this.selectedMenuVoice)));
    }


    @Override
    public boolean keyDown(int keycode) {

        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.UP: {
                if (selectedMenuVoice > 0) {
                    selectedMenuVoice--;
                }
                break;
            }
            case Input.Keys.S:
            case Input.Keys.DOWN: {
                if (selectedMenuVoice < currentMenu.getSubItems().length - 1) {
                    selectedMenuVoice++;
                }
                break;
            }
            case Input.Keys.X:
            case Input.Keys.K:
            case Input.Keys.ENTER: {
                handleSelection();
                break;
            }
        }
        return true;
    }

    /**
     * Handles selection event
     */
    private void handleSelection() {
        switch (currentMenu) {
            case NEW_GAME:
                handleNewGame();
                break;
            case MAIN:
                handleMain();
                break;
            case GAME_OVER:
                handleGameOver();
                break;
            case PLAY_GAME:
                handlePlayGame();
                break;
            default:
                handleDefaults();
                break;
        }
    }

    private void handlePlayGame() {
        switch (selectedMenuVoice) {
            case 0: {
                //New game
                selectedMenuVoice = 0;
                currentMenu = MenuItem.NEW_GAME;
                break;
            }
            case 1: {
                //Load game
                changeToGameScreen = true;
                break;
            }
            case 2: {
                //Back
                selectedMenuVoice = 0;
                currentMenu = MenuItem.MAIN;
                break;
            }
        }
    }

    private void handleMain() {
        switch (selectedMenuVoice) {
            case 0: {
                //New game
                selectedMenuVoice = 0;
                currentMenu = MenuItem.PLAY_GAME;
                break;
            }
            case 1: {
                //TODO credit
                break;
            }
            case 2: {
                //Exit game
                Gdx.app.exit();
                break;
            }
        }
    }

    private void handleNewGame() {
        switch (selectedMenuVoice) {
            case 0: {
                //Yes i'm sure
                saveFileManager.cleanSaveFile();
                changeToIntroScreen = true;
                break;
            }
            case 1: {
                //No, back to main
                currentMenu = MenuItem.MAIN;
                selectedMenuVoice = 0;
                break;
            }
        }
    }

    private void handleGameOver() {
        switch (selectedMenuVoice) {
            case 0: {
                //Yes, continue last save
                changeToGameScreen = true;
                break;
            }
            case 1: {
                //No, back to main
                changeToNextScreen = true;
                break;
            }
        }
    }

    /**
     * Just go to next Screen
     */
    private void handleDefaults() {
        changeToNextScreen = true;
    }

    public int getSelectedMenuVoice() {
        return selectedMenuVoice;
    }

    public boolean isChangeToGameScreen() {
        return changeToGameScreen;
    }

    public boolean isChangeToIntroScreen() {
        return changeToIntroScreen;
    }

    public boolean isChangeToNextScreen() {
        return changeToNextScreen;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
