package faust.lhipgame.game.instances.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import faust.lhipgame.game.gameentities.enums.ItemEnum;
import faust.lhipgame.game.gameentities.enums.POIEnum;
import faust.lhipgame.game.gameentities.impl.POIEntity;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.splash.SplashManager;
import faust.lhipgame.game.textbox.manager.TextBoxManager;
import faust.lhipgame.screens.GameScreen;

import java.util.Objects;

/**
 * POI instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class POIInstance extends GameInstance {


    private final boolean guaranteedMorgengabe; //flag gor guaranteed morgengabe
    private boolean enableFlicker = false; // flag for enable flickering
    private boolean mustFlicker = false;// flag that is true when the POI must be hidden
    private long startTime = 0; // flickering timer

    private boolean isAlreadyExamined;
    private final TextBoxManager textManager;
    private final SplashManager splashManager;


    public POIInstance(final TextBoxManager textManager, float x, float y, POIEnum poiType, final SplashManager splashManager, final AssetManager assetManager, boolean guaranteedMorgengabe) {
        super(new POIEntity(poiType, assetManager));
        this.textManager = textManager;
        this.startX = x;
        this.startY = y;
        this.isAlreadyExamined = false;
        this.splashManager = splashManager;
        this.guaranteedMorgengabe = guaranteedMorgengabe;
    }

    /**
     * Handles the examination from a Player Instance
     */
    public void examine(PlayerInstance player) {
        Objects.requireNonNull(player);

        //TODO add new examinations results
        String messageKey = ((POIEntity) this.entity).getMessageKey();

        //If is not already examined (if randomized, nothing CAN happen)
        if (!isAlreadyExamined && canCollectPOI(player) && ( !isRandomizedPOI() || MathUtils.randomBoolean())) {

            final ItemEnum itemGiven = ((POIEntity) this.entity).getItemGiven();

            //Let player find item
            if(itemGiven != null){
                player.foundItem(itemGiven);
            }

            //If has splash screen
            if(!((POIEntity) this.entity).getSplashKey().isEmpty()){
                String splashKey = ((POIEntity) this.entity).getSplashKey();

                //Holy lance has to different splashes based on pieces found
                if(itemGiven == ItemEnum.HOLY_LANCE){
                    splashKey+= "."+player.getHolyLancePieces();
                }

                // Show splash screen
                splashManager.setSplashToShow(splashKey);
            } else {
                // Just show message
                textManager.addNewTextBox(messageKey + POIEntity.FOUND_ITEM_MESSAGE_KEY_SUFFIX);
            }
        } else{
            // Just show message
            textManager.addNewTextBox(messageKey + POIEntity.EXAMINING_ITEM_MESSAGE_KEY_SUFFIX);
        }

        // Only on the first examination there is a chance to find something
        isAlreadyExamined = true;

    }

    private boolean canCollectPOI(PlayerInstance player) {

        switch (((POIEntity) entity).getType()){
            case SKELETON:
                return player.getFoundMorgengabes() < 9;
            case SOIL:
                return player.getHolyLancePieces() < 2; //Should be random only if not guaranteed!
            default:
                return true;
        }
    }

    /**
     *
     * @return true if the poi is randomized
     */
    private boolean isRandomizedPOI() {
        switch (((POIEntity) entity).getType()){
            case SKELETON:
                return !guaranteedMorgengabe; //Should be random only if not guaranteed!
            case BUSH:
                return true;
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
        // If flickering is not enabled or the flickering POI must be shown, draw the texture
        if (!this.enableFlicker || !mustFlicker) {
            batch.draw(((POIEntity) entity).getFrame(stateTime),
                    body.getPosition().x - POSITION_OFFSET,
                    body.getPosition().y - POSITION_OFFSET);
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
}
