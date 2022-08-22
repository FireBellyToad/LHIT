package com.faust.lhengine.game.renderer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.faust.lhengine.camera.CameraManager;
import com.faust.lhengine.game.PauseManager;
import com.faust.lhengine.game.hud.DarknessRenderer;
import com.faust.lhengine.game.hud.Hud;
import com.faust.lhengine.game.instances.impl.PlayerInstance;
import com.faust.lhengine.game.rooms.AbstractRoom;
import com.faust.lhengine.game.rooms.manager.RoomsManager;
import com.faust.lhengine.game.splash.SplashManager;
import com.faust.lhengine.game.textbox.manager.TextBoxManager;

import java.util.Objects;

/**
 * TopView World Renderer
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class TopViewWorldRenderer {

    private final SpriteBatch batch;
    private final CameraManager cameraManager;
    private final SplashManager splashManager;
    private final DarknessRenderer darknessRenderer;

    public TopViewWorldRenderer(SpriteBatch batch, CameraManager cameraManager, SplashManager splashManager, DarknessRenderer darknessRenderer) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(cameraManager);

        this.batch = batch;
        this.cameraManager = cameraManager;
        this.splashManager = splashManager;
        this.darknessRenderer = darknessRenderer;
    }

    /**
     * Draws the background color and terrain tiles
     */
    public void drawBackground(AbstractRoom currentRoom) {
        batch.begin();
        cameraManager.renderBackground();
        batch.end();
        currentRoom.drawRoomTerrain();
    }

    /**
     * Draws all the overlays
     *
     * @param stateTime
     * @param hud
     * @param player
     * @param currentRoom
     * @param pauseManager
     * @param textManager
     */
    public void drawOverlays(float stateTime, Hud hud, PlayerInstance player, AbstractRoom currentRoom, PauseManager pauseManager, TextBoxManager textManager) {
        //Draw overlay tiles
        currentRoom.drawRoomOverlay();

        // Draw splash XOR hud
        if (splashManager.isDrawingSplash()) {
            splashManager.drawSplash(batch,stateTime);
        } else {
            darknessRenderer.drawDarkness(batch, player.getBody().getPosition(), cameraManager.getCamera());
            hud.drawHud(batch, player, cameraManager.getCamera());
            if (pauseManager.isGamePaused()) {
                pauseManager.draw(batch,cameraManager.getCamera());
            }
        }
        // draw text
        textManager.renderTextBoxes(batch, cameraManager.getCamera());

    }

    /**
     * Draws all the room content
     *
     * @param stateTime
     * @param currentRoom
     */
    public void drawRoomAndContents(float stateTime, AbstractRoom currentRoom) {
        currentRoom.drawRoomContents(batch, stateTime, cameraManager.getCamera());
    }

}
