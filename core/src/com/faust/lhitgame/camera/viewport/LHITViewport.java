package com.faust.lhitgame.camera.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.faust.lhitgame.LHITGame;

/**
 * Custom viewport for fullscreen handling
 */
public class LHITViewport extends ScalingViewport {

    public LHITViewport(Camera camera) {
        super(null, LHITGame.GAME_WIDTH, LHITGame.GAME_HEIGHT, camera);
    }

    /**
     * Override update logic, using Scaling.fillY calculation but with floor to nearest integer
     * for fixing sprite tearing
     *
     * @param screenWidth
     * @param screenHeight
     * @param centerCamera
     */
    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {

        //Use fillY logic, but scaling down to nearest integer
        Vector2 scaled = new Vector2();
        float scale = MathUtils.round(screenHeight / getWorldHeight());

        scaled.x = getWorldWidth() * scale;
        scaled.y = getWorldHeight() * scale;

        //Scale viewport
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);

        //Normal flow from now on
        // Center.
        setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);

        apply(centerCamera);
    }
}
