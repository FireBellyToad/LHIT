package faust.lhipgame.gameentities.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import faust.lhipgame.echoes.enums.EchoesActorType;
import faust.lhipgame.gameentities.AnimatedEntity;
import faust.lhipgame.gameentities.enums.GameBehavior;

import java.util.*;


/**
 * Echo Actor entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorEntity extends AnimatedEntity {

    private final EchoesActorType echoesActorType;

    //Seconds of each step, ordered
    private final Map<GameBehavior, Float> secondsPerStep = new LinkedHashMap<>();
    private final List<GameBehavior> stepOrder = new ArrayList<>();

    public EchoActorEntity(EchoesActorType echoesActorType) {
        super(new Texture(echoesActorType.getSpriteFilename()));
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
            float seconds = s.getFloat("seconds");
            Objects.requireNonNull(behaviour);
            secondsPerStep.put(behaviour, seconds);
            stepOrder.add(behaviour);
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
}
