package faust.lhitgame.game.gameentities.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhitgame.game.echoes.enums.EchoesActorType;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.EnemyEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;

import java.util.*;


/**
 * Echo Actor entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorEntity extends AnimatedEntity {

    private final EchoesActorType echoesActorType;
    private Sound startingSound;
    private int precalculatedRows =5;

    //Each step, ordered
    private final List<GameBehavior> stepOrder = new ArrayList<>();
    protected final Map<GameBehavior, String> textBoxPerStep = new HashMap<>();
    protected final Map<GameBehavior, DirectionEnum> mustMoveInStep = new HashMap<>();
    protected final Map<GameBehavior, GameBehavior> gotoToStepFromStep = new HashMap<>();
    protected final Map<GameBehavior, Integer> speedInStep = new HashMap<>();
    protected final Map<GameBehavior, EnemyEnum> untilAtLeastOneFromStep = new HashMap<>();

    public EchoActorEntity(EchoesActorType echoesActorType, AssetManager assetManager) {
        super(assetManager.get(echoesActorType.getSpriteFilename()));
        this.echoesActorType = echoesActorType;
        if (Objects.nonNull(echoesActorType.getSoundFileName())) {
            this.startingSound = assetManager.get(echoesActorType.getSoundFileName());
        }
        if(echoesActorType.equals(EchoesActorType.HORROR_BODY)){
            Gdx.app.log("HERE","JERER");
        }
        loadEchoActorSteps();
    }

    /**
     * Initialize steps and seconds per step
     */
    private void loadEchoActorSteps() {

        JsonValue parsedSteps = new JsonReader().parse(Gdx.files.internal("scripts/" + echoesActorType.getFilename())).get("steps");

        Objects.requireNonNull(parsedSteps);

        //Precalculate rows from steps
        precalculatedRows = parsedSteps.size;

        TextureRegion[] allFrames = getFramesFromTexture();

        int stepCounter = 0;
        for (JsonValue s : parsedSteps) {
            GameBehavior behaviour = GameBehavior.getFromOrdinal(stepCounter);
            Objects.requireNonNull(behaviour);
            stepOrder.add(behaviour);

            if (s.has("textBoxKey")) {
                textBoxPerStep.put(behaviour, s.getString("textBoxKey"));
            }
            if (s.has("move")) {
                mustMoveInStep.put(behaviour, DirectionEnum.getFromString(s.getString("move")));
                speedInStep.put(behaviour, s.getInt("speed"));
            }

            if (s.has("goToStep")) {
                gotoToStepFromStep.put(behaviour, GameBehavior.getFromOrdinal(s.getInt("goToStep")));

                if (s.has("untilAtLeastOne")) {
                    EnemyEnum enemyEnum = EnemyEnum.getFromString(s.getString("untilAtLeastOne"));
                    Objects.requireNonNull(enemyEnum);
                    untilAtLeastOneFromStep.put(behaviour, enemyEnum);
                }
            }
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
     * Get text box to show given "step"
     *
     * @param step
     * @return can be null
     */
    public String getTextBoxPerStep(GameBehavior step) {
        return textBoxPerStep.get(step);
    }

    /**
     * @param step
     * @return true if must move in this step
     */
    public Boolean mustMoveInStep(GameBehavior step) {
        return mustMoveInStep.containsKey(step);
    }

    /**
     * @param step
     * @return true if must move in this step
     */
    public Integer getSpeedInStep(GameBehavior step) {
        return speedInStep.get(step);
    }

    /**
     * @param step
     * @return DirectionEnum of movement
     */
    public DirectionEnum getDirection(GameBehavior step) {
        return mustMoveInStep.get(step);
    }

    /**
     * @param fromStep
     * @return the step
     */
    public GameBehavior getGotoToStepFromStep(GameBehavior fromStep) {
        return gotoToStepFromStep.get(fromStep);
    }

    /**
     *
     * @param fromStep
     * @return
     */
    public EnemyEnum getUntilAtLeastOneFromStep(GameBehavior fromStep) {
        return untilAtLeastOneFromStep.get(fromStep);
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
