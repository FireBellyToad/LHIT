package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.faust.lhitgame.game.gameentities.enums.ItemEnum;
import com.faust.lhitgame.game.gameentities.enums.POIEnum;
import com.faust.lhitgame.game.gameentities.impl.POIEntity;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.splash.SplashManager;
import com.faust.lhitgame.game.textbox.manager.TextBoxManager;
import com.faust.lhitgame.screens.GameScreen;

import java.util.Objects;

/**
 * POI instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class POIInstance extends GameInstance {


    private final boolean guaranteedGoldcross; //flag gor guaranteed goldcross
    private boolean enableFlicker = false; // flag for enable flickering
    private boolean mustFlicker = false;// flag that is true when the POI must be hidden
    private long startTime = 0; // flickering timer
    private final int poiIdInMap; // POI id in map

    private boolean isAlreadyExamined;
    private final boolean mustTriggerAfterExamination;
    private final TextBoxManager textManager;
    private final SplashManager splashManager;


    public POIInstance(final TextBoxManager textManager, float x, float y, POIEnum poiType, int id, final SplashManager splashManager, final AssetManager assetManager, boolean guaranteedGoldcross) {
        super(new POIEntity(poiType, assetManager));
        this.textManager = textManager;
        this.startX = x;
        this.startY = y;
        this.isAlreadyExamined = false;
        this.splashManager = splashManager;
        this.guaranteedGoldcross = guaranteedGoldcross;
        this.poiIdInMap = id;
        this.mustTriggerAfterExamination = poiType.mustTriggerAfterExamination();

        this.alwaysInBackground = poiType.equals(POIEnum.ECHO_CORPSE) || poiType.equals(POIEnum.SKELETON) || poiType.equals(POIEnum.BURNT_PAPER);
    }

    /**
     * Handles the examination from a Player Instance
     */
    public void examine(PlayerInstance player) {
        Objects.requireNonNull(player);

        //TODO add new examinations results
        String messageKey = ((POIEntity) this.entity).getMessageKey();

        //If is not already examined (if randomized, nothing CAN happen)
        if (!isAlreadyExamined && canCollectPOI(player) && (!isRandomizedPOI() || MathUtils.randomBoolean())) {

            final ItemEnum itemGiven = ((POIEntity) this.entity).getItemGiven();

            //Let player find item
            if (Objects.nonNull(itemGiven)) {
                player.foundItem(itemGiven);
            }

            //If has splash screen
            if (!((POIEntity) this.entity).getSplashKey().isEmpty()) {
                String splashKey = ((POIEntity) this.entity).getSplashKey();

                //Holy lance has to different splashes based on pieces found
                if (itemGiven == ItemEnum.HOLY_LANCE) {
                    splashKey += "." + player.getHolyLancePieces();
                }

                // Show splash screen
                splashManager.setSplashToShow(splashKey);
                Gdx.input.setInputProcessor(splashManager);
            } else {
                // Just show message
                textManager.addNewTextBox(messageKey + POIEntity.FOUND_ITEM_MESSAGE_KEY_SUFFIX);
            }
        } else {
            // Just show message
            textManager.addNewTextBox(messageKey + POIEntity.EXAMINING_ITEM_MESSAGE_KEY_SUFFIX);
        }

        // Only on the first examination there is a chance to find something
        //BAPTISMAL can be examined again if player as no statue
        //TODO remove hardcoding
        isAlreadyExamined = !POIEnum.BAPTISMAL.equals(((POIEntity) this.entity).getType()) || player.hasStatue();

    }

    private boolean canCollectPOI(PlayerInstance player) {

        switch (((POIEntity) entity).getType()) {
            case SKELETON:
                return player.getFoundCrosses() < 9;
            case SOIL:
                return player.getHolyLancePieces() < 2; //Should be random only if not guaranteed!
            case BAPTISMAL:
                return player.hasStatue();
            default:
                return true;
        }
    }

    /**
     * @return true if the poi is randomized
     */
    private boolean isRandomizedPOI() {
        switch (((POIEntity) entity).getType()) {
            case SKELETON:
                return !guaranteedGoldcross; //Should be random only if not guaranteed!
            default:
                return false;
        }
    }

    @Override
    public void createBody(World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4, 2);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
        fixtureDef.isSensor = true;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }


    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        batch.begin();
        Vector2 drawPosition = adjustPosition();
        // If flickering is not enabled or the flickering POI must be shown, draw the texture
        if (!this.enableFlicker || !mustFlicker) {
            batch.draw(((POIEntity) entity).getFrame(stateTime),
                    drawPosition.x - POSITION_OFFSET,
                    drawPosition.y - POSITION_OFFSET);
        }

        // Every 1/8 seconds alternate between showing and hiding the texture to achieve flickering effect
        if (this.enableFlicker && TimeUtils.timeSinceNanos(startTime) > GameScreen.FLICKER_DURATION_IN_NANO) {
            mustFlicker = !mustFlicker;

            // restart flickering timer
            startTime = TimeUtils.nanoTime();
        }
        batch.end();
    }

    @Override
    //Player is never disposable
    public boolean isDisposable() {
        return false;
    }

    public void setEnableFlicker(boolean enableFlicker) {
        this.enableFlicker = enableFlicker;
    }

    // Handling POI states
    public boolean isAlreadyExamined() {
        return isAlreadyExamined;
    }

    public void setAlreadyExamined(boolean alreadyExamined) {
        isAlreadyExamined = alreadyExamined;
    }

    public POIEnum getType() {
        return ((POIEntity) entity).getType();
    }

    public int getPoiIdInMap() {
        return poiIdInMap;
    }

    public boolean mustTriggerAfterExamination() {
        return mustTriggerAfterExamination;
    }
}
