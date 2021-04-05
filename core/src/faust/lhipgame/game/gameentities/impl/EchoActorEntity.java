package faust.lhipgame.game.gameentities.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
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

    //Each step, ordered
    private final List<GameBehavior> stepOrder = new ArrayList<>();
    protected Map<GameBehavior, String> textBoxPerStep = new HashMap<>();

    public EchoActorEntity(EchoesActorType echoesActorType, AssetManager assetManager) {
        super(assetManager.get(echoesActorType.getSpriteFilename()));
        this.echoesActorType = echoesActorType;

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
        });

    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFrames = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] walkFrames = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns() * 2);

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimation(new Animation<>(FRAME_DURATION, idleFrames), GameBehavior.IDLE);
        addAnimation(new Animation<>(FRAME_DURATION, walkFrames), GameBehavior.WALK);

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
        return 2;
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

}
