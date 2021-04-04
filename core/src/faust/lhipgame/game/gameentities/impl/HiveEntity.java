package faust.lhipgame.game.gameentities.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.enums.Direction;
import faust.lhipgame.game.gameentities.enums.GameBehavior;

import java.util.Arrays;

/**
 *
 * Hive enemy Entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class HiveEntity extends AnimatedEntity {

    private final Sound hurtCry;
    private final Sound deathCry;

    public HiveEntity(AssetManager assetManager) {
        super(assetManager.get("sprites/hive_sheet.png"));
        hurtCry = assetManager.get("sounds/SFX_hit&damage2.ogg");
        deathCry = assetManager.get("sounds/SFX_creatureDie4.ogg");
    }

    @Override
    protected void initAnimations() {

        TextureRegion[]  allFrames = getFramesFromTexture();

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimation(new Animation<>(FRAME_DURATION, allFrames), GameBehavior.IDLE);
        addAnimation(new Animation<>(FRAME_DURATION, allFrames), GameBehavior.HURT);

    }

    @Override
    protected int getTextureColumns() {
        return 9;
    }

    @Override
    protected int getTextureRows() {
        return 1;
    }

    public void playHurtCry() {
        hurtCry.play();
    }

    public void playDeathCry() {
        deathCry.play();
    }
}
