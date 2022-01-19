package com.faust.lhitgame.game.gameentities.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.faust.lhitgame.game.echoes.enums.EchoCommandsEnum;
import com.faust.lhitgame.game.echoes.enums.EchoesActorType;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.*;


/**
 * Echo Actor entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorEntity extends AnimatedEntity {

    private final EchoesActorType echoesActorType;
    private Sound startingSound;
    private int precalculatedRows = 5;

    //Each step, ordered
    private final List<GameBehavior> stepOrder = new ArrayList<>();

    public EchoActorEntity(EchoesActorType echoesActorType, AssetManager assetManager) {
        super(assetManager.get(echoesActorType.getSpriteFilename()));
        this.echoesActorType = echoesActorType;
        if (Objects.nonNull(echoesActorType.getSoundFileName())) {
            this.startingSound = assetManager.get(echoesActorType.getSoundFileName());
        }

        loadEchoActorSteps();
    }

    /**
     * Initialize steps and seconds per step
     */
    private void loadEchoActorSteps() {
        final String fileAsString = Gdx.files.internal("scripts/" + echoesActorType.getFilename()).readString();

        precalculatedRows = fileAsString.split("function doStep\\d").length -1;

        TextureRegion[] allFrames = getFramesFromTexture();

        int stepCounter = 0;
        while (stepCounter < precalculatedRows) {

            GameBehavior behaviour = GameBehavior.getFromOrdinal(stepCounter);
            Objects.requireNonNull(behaviour);
            stepOrder.add(behaviour);

            addAnimation(new Animation<>(FRAME_DURATION, Arrays.copyOfRange(allFrames, getTextureColumns() * stepCounter, getTextureColumns() * (stepCounter + 1))), behaviour);

            stepCounter++;
        }

    }

    @Override
    protected void initAnimations() {
        //Nothing to do here... initializing animation after load
    }

    public List<GameBehavior> getStepOrder() {
        return stepOrder;
    }

    @Override
    protected int getTextureColumns() {
        return 6;
    }

    @Override
    protected int getTextureRows() {
        return precalculatedRows;
    }

    public EchoesActorType getEchoesActorType() {
        return echoesActorType;
    }

    /**
     * Play starting sound if valid
     */
    public void playStartingSound() {
        if (Objects.nonNull(startingSound)) {
            startingSound.play(0.75f);
        }
    }

    public void stopStartingSound() {
        if (Objects.nonNull(startingSound)) {
            startingSound.stop();
        }
    }

}
