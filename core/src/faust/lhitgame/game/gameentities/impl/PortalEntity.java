package faust.lhitgame.game.gameentities.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.enums.GameBehavior;

import java.util.Arrays;

/**
 *
 * Portal Entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class PortalEntity extends AnimatedEntity {

    public PortalEntity(AssetManager assetManager) {
        super(assetManager.get("sprites/portal_sheet.png"));
    }

    @Override
    protected void initAnimations() {

        TextureRegion[]  allFrames = getFramesFromTexture();

        TextureRegion[] idleFrames = Arrays.copyOfRange(allFrames, 0, getTextureColumns());

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimation(new Animation<>(FRAME_DURATION, idleFrames), GameBehavior.IDLE);

    }

    @Override
    protected int getTextureColumns() {
        return 8;
    }

    @Override
    protected int getTextureRows() {
        return 1;
    }

}
