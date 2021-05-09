package faust.lhipgame.game.gameentities.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhipgame.game.echoes.enums.EchoesActorType;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.enums.GameBehavior;

import java.util.*;


/**
 * Echo Actor entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorEntity extends AnimatedEntity {

    private final EchoesActorType echoesActorType;
    private final Sound startingSound;

    //Each step, ordered
    private final List<GameBehavior> stepOrder = new ArrayList<>();
    protected Map<GameBehavior, String> textBoxPerStep = new HashMap<>();
    protected Map<GameBehavior, Boolean> mustMoveInStep = new HashMap<>();
    protected Map<GameBehavior, GameBehavior> gotoToStepFromStep = new HashMap<>();

    public EchoActorEntity(EchoesActorType echoesActorType, AssetManager assetManager) {
        super(assetManager.get(echoesActorType.getSpriteFilename()));
        this.echoesActorType = echoesActorType;
        //TODO improve
        this.startingSound = assetManager.get("sounds/horror_scream.ogg");
        loadEchoActorSteps();
    }

    /**
     * Initialize steps and seconds per step
     */
    private void loadEchoActorSteps() {

        JsonValue parsedSteps = new JsonReader().parse(Gdx.files.internal("scripts/" + echoesActorType.getFilename())).get("steps");

        Objects.requireNonNull(parsedSteps);

        parsedSteps.forEach((s) -> {
            GameBehavior behaviour = GameBehavior.getFromString(s.getString("behaviour"));
            Objects.requireNonNull(behaviour);
            stepOrder.add(behaviour);

            if (s.has("textBoxKey")) {
                textBoxPerStep.put(behaviour, s.getString("textBoxKey"));
            }
            mustMoveInStep.put(behaviour,s.has("move"));

            if (s.has("goToStep")) {
                gotoToStepFromStep.put(behaviour, GameBehavior.getFromString(s.getString("goToStep")));
            }
        });

    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFrames = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] walkFrames = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns() * 2);
        TextureRegion[] attackFrames = Arrays.copyOfRange(allFrames, getTextureColumns()*2, getTextureColumns() * 3);
        TextureRegion[] hurtFrames = Arrays.copyOfRange(allFrames, getTextureColumns()*3, getTextureColumns() * 4);
        TextureRegion[] deadFrames = Arrays.copyOfRange(allFrames, getTextureColumns()*4, getTextureColumns() * 5);

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimation(new Animation<>(FRAME_DURATION, idleFrames), GameBehavior.IDLE);
        addAnimation(new Animation<>(FRAME_DURATION, walkFrames), GameBehavior.WALK);
        addAnimation(new Animation<>(FRAME_DURATION, attackFrames), GameBehavior.ATTACK);;
        addAnimation(new Animation<>(FRAME_DURATION, hurtFrames), GameBehavior.HURT);
        addAnimation(new Animation<>(FRAME_DURATION, deadFrames), GameBehavior.DEAD);


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
        return 5;
    }

    public EchoesActorType getEchoesActorType() {
        return echoesActorType;
    }
    /**
     * Get text box to show given "step"
     * @param step
     * @return can be null
     */
    public String getTextBoxPerStep(GameBehavior step){
        return textBoxPerStep.get(step);
    }

    /**
     *
     * @param step
     * @return true if must move in this step
     */
    public Boolean mustMoveInStep(GameBehavior step) {
        return mustMoveInStep.get(step);
    }

    /**
     *
     * @param fromStep
     * @return the step
     */
    public GameBehavior getGotoToStepFromStep(GameBehavior fromStep) {
        return gotoToStepFromStep.get(fromStep);
    }

    public void playStartingSound() {
        startingSound.play(0.5f);
    }

}
