package faust.lhitgame.game.gameentities.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhitgame.game.echoes.enums.EchoCommandsEnum;
import faust.lhitgame.game.echoes.enums.EchoesActorType;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.EnemyEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.utils.ValidEcho;

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
    protected final Map<GameBehavior, Map<EchoCommandsEnum, Object>> commands = new HashMap<>();

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

        JsonValue parsedSteps = new JsonReader().parse(Gdx.files.internal("scripts/" + echoesActorType.getFilename())).get("steps");

        ValidEcho.validate(parsedSteps, echoesActorType.getFilename());

        //Precalculate rows from steps
        precalculatedRows = parsedSteps.size;

        TextureRegion[] allFrames = getFramesFromTexture();

        int stepCounter = 0;
        for (JsonValue s : parsedSteps) {

            GameBehavior behaviour = GameBehavior.getFromOrdinal(stepCounter);
            Objects.requireNonNull(behaviour);
            stepOrder.add(behaviour);

            commands.put(behaviour, extractValue(new HashMap<>(), s.child));
            addAnimation(new Animation<>(FRAME_DURATION, Arrays.copyOfRange(allFrames, getTextureColumns() * stepCounter, getTextureColumns() * (stepCounter + 1))), behaviour);

            stepCounter++;
        }

        Gdx.app.log("DEBUG", "Stuff");
    }

    /**
     * Extracts all values and put them in map
     *
     * @param values
     * @param jsonValue
     */
    private Map<EchoCommandsEnum, Object> extractValue(final Map<EchoCommandsEnum, Object> values, final JsonValue jsonValue) {

        if (Objects.nonNull(jsonValue)) {

            EchoCommandsEnum extractedCommand = EchoCommandsEnum.getFromCommandString(jsonValue.name());

            if (Objects.nonNull(extractedCommand)) {

                if (extractedCommand.getValueClass().equals(String.class)) {
                    values.put(extractedCommand, jsonValue.asString());
                } else if (extractedCommand.getValueClass().equals(Integer.class)) {
                    values.put(extractedCommand, jsonValue.asInt());
                } else if (extractedCommand.getValueClass().equals(EchoCommandsEnum.class)) {
                    extractValue(values, jsonValue.child);
                }

                // Go to next
                if (Objects.nonNull(jsonValue.next)) {
                    extractValue(values, jsonValue.next);
                }
            }
        }
        return values;
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

    //FIXME all getters

    /**
     * Get commands to do in given "step"
     *
     * @param step
     * @return
     */
    public  Map<EchoCommandsEnum, Object> getCommandsForStep(GameBehavior step){
        return commands.containsKey(step) ? commands.get(step) : null;
    }

    /**
     * Get text box to show given "step"
     *
     * @param step
     * @return can be null
     */
    public String getTextBoxPerStep(GameBehavior step) {
        if(Objects.isNull(commands.get(step))){
            return null;
        }
        return (String) commands.get(step).get(EchoCommandsEnum.TEXTBOX_KEY);
    }

    /**
     * @param step
     * @return true if must move in this step
     */
    public Boolean mustMoveInStep(GameBehavior step) {
        if(Objects.isNull(commands.get(step))){
            return null;
        }
        return commands.get(step).containsKey(EchoCommandsEnum.DIRECTION) && commands.get(step).containsKey(EchoCommandsEnum.SPEED);
    }

    /**
     * @param step
     * @return true if must move in this step
     */
    public Integer getSpeedInStep(GameBehavior step) {
        if(Objects.isNull(commands.get(step))){
            return null;
        }
        return (Integer) commands.get(step).get(EchoCommandsEnum.SPEED);
    }

    /**
     * @param step
     * @return DirectionEnum of movement
     */
    public DirectionEnum getDirection(GameBehavior step) {
        if(Objects.isNull(commands.get(step))){
            return null;
        }
        String direction = (String) commands.get(step).get(EchoCommandsEnum.DIRECTION);

        return DirectionEnum.valueOf(direction);
    }

    /**
     * @param fromStep
     * @return the step
     */
    public GameBehavior getGotoToStepFromStep(GameBehavior fromStep) {
        if(Objects.isNull(commands.get(fromStep))){
            return null;
        }
        Integer ord = (Integer) commands.get(fromStep).get(EchoCommandsEnum.STEP);

        return Objects.isNull(ord) ? null : GameBehavior.getFromOrdinal(ord);
    }

    /**
     * @param fromStep
     * @return
     */
    public EnemyEnum getUntilAtLeastOneKillableFromStep(GameBehavior fromStep) {
        if(Objects.isNull(commands.get(fromStep))){
            return null;
        }
        String killableName = (String) commands.get(fromStep).get(EchoCommandsEnum.UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE);

        return Objects.isNull(killableName) ? null : EnemyEnum.valueOf(killableName);
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
