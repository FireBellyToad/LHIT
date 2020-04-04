package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import faust.lhipgame.gameentities.POIEntity;

import java.util.Objects;

public class POIInstance extends GameInstance {

    private static final long FLICKER_DURATION_IN_NANO = 125000000; // 1/4 second
    private boolean enableFlicker = false; // flag for enable flickering
    private boolean mustFlicker = false;// flag that is true when the POI must be hidden
    private long startTime = 0;

    final Color color = new Color(1, 1, 1, 0);

    public POIInstance() {
        super(new POIEntity());
    }

    public void examine() {
        //TODO
        System.out.println("POI EXAMINED");
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);

        mustFlicker = TimeUtils.timeSinceNanos(startTime) > FLICKER_DURATION_IN_NANO;

        // If flickering is not enabled or the flickering POI must be shown, draw the texture
        if(!this.enableFlicker || mustFlicker){
            batch.draw(entity.getTexture(), body.getPosition().x, body.getPosition().y);

            // restart flickering timer
            startTime = TimeUtils.nanoTime();
        }

    }

    public void setEnableFlicker(boolean enableFlicker) {
        this.enableFlicker = enableFlicker;
    }
}
