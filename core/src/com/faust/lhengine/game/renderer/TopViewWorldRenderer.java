package com.faust.lhengine.game.renderer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.faust.lhengine.camera.CameraManager;
import com.faust.lhengine.game.PauseManager;
import com.faust.lhengine.game.hud.DarknessRenderer;
import com.faust.lhengine.game.hud.Hud;
import com.faust.lhengine.game.instances.impl.PlayerInstance;
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
    private final RoomsManager roomsManager;
    private final DarknessRenderer darknessRenderer;

    public TopViewWorldRenderer(SpriteBatch batch, CameraManager cameraManager, SplashManager splashManager, RoomsManager roomsManager, AssetManager assetManager) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(cameraManager);

        this.batch = batch;
        this.cameraManager = cameraManager;
        this.splashManager = splashManager;
        this.roomsManager = roomsManager;
        this.darknessRenderer = new DarknessRenderer(assetManager);
    }

    /**
     * Draws the background color and terrain tiles
     */
    public void drawBackground() {
        batch.begin();
        cameraManager.renderBackground();
        batch.end();
        roomsManager.drawCurrentRoomBackground();
    }
    

    public void drawOverlays(float stateTime, Hud hud, PlayerInstance player, PauseManager pauseManager, TextBoxManager textManager) {
        //Draw overlay tiles
        roomsManager.drawCurrentRoomOverlays();

        // Draw splash XOR hud
        if (splashManager.isDrawingSplash()) {
            splashManager.drawSplash(batch,stateTime);
        } else {
            darknessRenderer.drawDarkness(batch, player, cameraManager.getCamera());
            hud.drawHud(batch, player, cameraManager.getCamera());
            if (pauseManager.isGamePaused()) {
                pauseManager.draw(batch,cameraManager.getCamera());
            }
        }
        // draw text
        textManager.renderTextBoxes(batch, cameraManager.getCamera());
    }

    public void drawRoomAndContents(float stateTime) {
        roomsManager.drawCurrentRoomContents(batch, stateTime);
    }

}
