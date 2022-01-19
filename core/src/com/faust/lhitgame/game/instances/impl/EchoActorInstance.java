package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.faust.lhitgame.game.echoes.enums.EchoCommandsEnum;
import com.faust.lhitgame.game.echoes.enums.EchoesActorType;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.enums.DirectionEnum;
import com.faust.lhitgame.game.gameentities.enums.EnemyEnum;
import com.faust.lhitgame.game.gameentities.enums.GameBehavior;
import com.faust.lhitgame.game.gameentities.enums.POIEnum;
import com.faust.lhitgame.game.gameentities.impl.EchoActorEntity;
import com.faust.lhitgame.game.instances.AnimatedInstance;
import com.faust.lhitgame.game.instances.Spawner;
import com.faust.lhitgame.game.instances.interfaces.Damager;
import com.faust.lhitgame.game.instances.interfaces.Interactable;
import com.faust.lhitgame.game.instances.interfaces.Killable;
import com.faust.lhitgame.game.rooms.RoomContent;
import com.faust.lhitgame.game.rooms.enums.MapLayersEnum;
import com.faust.lhitgame.game.world.manager.CollisionManager;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaNil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.List;
import java.util.Objects;


/**
 * Class for Echo Actors Instances
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorInstance extends AnimatedInstance implements Interactable, Damager {

    private final Globals globals = JsePlatform.standardGlobals();
    private boolean mustRemoveFromRoom = false;
    private boolean showTextBox = true;
    private float deltaTime = 0; // Time delta between step start and current
    private boolean isEchoActive;
    private final Spawner spawner;
    private boolean isInvisible = false;

    private String textBoxToShow;
    private String layerToShow;
    private boolean doneCommands = false;


    public EchoActorInstance(EchoesActorType echoesActorType, float x, float y, AssetManager assetManager, Spawner spawner) {
        super(new EchoActorEntity(echoesActorType, assetManager));
        this.startX = x;
        this.startY = y;
        this.isEchoActive = false;
        this.spawner = spawner;

        LuaValue script = globals.loadfile("scripts/" + echoesActorType.getFilename());

        // very important step. subsequent calls to script method do not work if the
        // chunk
        // is not called here
        script.call();

        //get first step
        this.currentBehavior = ((EchoActorEntity) this.entity).getStepOrder().get(0);
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        // If must be removed, avoid logic
        if (!mustRemoveFromRoom) {

            //Check if echo is active. On first iteration set to true
            if (!isEchoActive) {
                isEchoActive = true;
            }

            //initialize deltatime
            if (deltaTime == 0)
                deltaTime = stateTime;

            executeCommands(roomContent, stateTime);

        }
    }

    /**
     * Executes all the commands in one step
     *
     * @param roomContent the room contents
     * @param stateTime   stateTime of the main loop
     */
    private void executeCommands(RoomContent roomContent, float stateTime) {

        final List<GameBehavior> stepOrder = ((EchoActorEntity) entity).getStepOrder();
        String stepName = "doStep" + stepOrder.indexOf(currentBehavior);
        int stepReference = stepOrder.indexOf(currentBehavior);
        //All commands to do in this step
        if(!doneCommands){
            doneCommands = true;
            Gdx.app.log("DEBUG", "Echo Actor " + stepName + " will be executed ");
            globals.get(stepName).invoke(
                    new LuaValue[]{CoerceJavaToLua.coerce(this),
                            CoerceJavaToLua.coerce(roomContent)});
        }


        //If animation is finished pass to the next step
        if (((EchoActorEntity) this.entity).isAnimationFinished(currentBehavior, mapStateTimeFromBehaviour(stateTime))) {;

            deltaTime = stateTime;
            //reset check
            doneCommands = false;

            //If is not last step
            if (stepReference + 1 < stepOrder.size()) {
                //Go to next
                currentBehavior = ((EchoActorEntity) entity).getStepOrder().get(stepReference + 1);
                showTextBox = true;

            } else {
                //else do last step
                mustRemoveFromRoom = true;
                final LuaValue endStep = globals.get("onEchoEnd");
                //If has event on echo end, do them
                if (endStep.isfunction()) {
                    endStep.invoke(
                            new LuaValue[]{CoerceJavaToLua.coerce(this),
                                    CoerceJavaToLua.coerce(roomContent)});
                }

                Gdx.app.log("DEBUG", "Echo Actor " + ((EchoActorEntity) entity).getEchoesActorType() + " must be removed ");
            }
        }
    }

    /**
     * @param index
     */
    public void setNewIndex(int index) {
        currentBehavior = ((EchoActorEntity) entity).getStepOrder().get(index);
    }

    /**
     * New step from logics
     *
     * @param checkToDo
     * @param valueToCheck
     * @param roomContent
     * @return
     */
    public boolean isCheckTrue(String checkToDo, String valueToCheck, RoomContent roomContent) {

        final EchoCommandsEnum checkEnum = EchoCommandsEnum.valueOf(checkToDo);
        boolean mustGoToStep = true;
        //Check condition on until Player has at least less then N damage (priority on other checks)
        if (EchoCommandsEnum.UNTIL_PLAYER_DAMAGE_IS_MORE_THAN.equals(checkEnum)) {
            //Extract value of damage
            final int value = Integer.valueOf(valueToCheck);
            mustGoToStep = roomContent.player.getDamage() <= value;
        }

        //Check condition on until there is at least one enemy of type is alive in room
        if (EchoCommandsEnum.UNTIL_AT_LEAST_ONE_KILLABLE_ALIVE.equals(checkEnum)) {
            //Extract instance class from enum and do check
            final EnemyEnum enemyEnum = EnemyEnum.valueOf(valueToCheck);
            final Class<? extends AnimatedInstance> enemyClass = enemyEnum.getInstanceClass();
            mustGoToStep = roomContent.enemyList.stream().anyMatch(e -> enemyClass.equals(e.getClass()) && !((Killable) e).isDead());
        }

        if (EchoCommandsEnum.UNTIL_AT_LEAST_ONE_POI_EXAMINABLE.equals(checkEnum)) {
            //Extract Poi type and do check
            final POIEnum poiEnum = POIEnum.valueOf(valueToCheck);
            mustGoToStep = roomContent.poiList.stream().anyMatch(poi -> poiEnum.equals(poi.getType()) && poi.isAlreadyExamined());
        }

        return mustGoToStep;
    }

    /**
     * Spawn instance if doable
     *
     * @param thingName thing to spawn
     */
    public void spawnInstance(String thingName, Float x, Float y, Boolean useRelative) {

        if (Objects.isNull(thingName)) {
            return;
        }

        EnemyEnum enemyEnum = null;
        POIEnum poiEnum = null;

        try {
            enemyEnum = EnemyEnum.valueOf(thingName);
        } catch (Exception e) {
            //Nothing to do here...
        }

        try {
            poiEnum = POIEnum.valueOf(thingName);
        } catch (Exception e) {
            //Nothing to do here...
        }

        //Set spawn coordinates
        float spawnX = startX;
        float spawnY = startY;

        if (Objects.nonNull(x)) {
            spawnX = useRelative ? spawnX + x : x;
        }
        if (Objects.nonNull(y)) {
            spawnY = useRelative ? spawnY + y : y;
        }

        if (Objects.nonNull(enemyEnum)) {
            spawner.spawnInstance(enemyEnum.getInstanceClass(), spawnX, spawnY, enemyEnum.name());
        } else if (Objects.nonNull(poiEnum)) {
            spawner.spawnInstance(POIInstance.class, spawnX, spawnY, poiEnum.name());
        }
    }

    /**
     * @param directionName      string name
     * @param speed
     */
    public void move(String directionName, float speed) {
        DirectionEnum direction = DirectionEnum.valueOf(directionName);
        Vector2 velocityVector;
        switch (direction) {
            case UP: {
                velocityVector = new Vector2(0, speed);
                break;
            }
            case DOWN: {
                velocityVector = new Vector2(0, -speed);
                break;
            }
            case RIGHT: {
                velocityVector = new Vector2(speed, 0);
                break;
            }
            case LEFT: {
                velocityVector = new Vector2(-speed, 0);
                break;
            }
            default: {
                velocityVector = new Vector2(0, 0);
                break;
            }
        }
        body.setLinearVelocity(velocityVector);
    }

    private float mapStateTimeFromBehaviour(float stateTime) {
        switch (((EchoActorEntity) entity).getEchoesActorType()) {
            case DEAD_HAND:
            case DEAD_DOUBLE_HAND:
                //Glitchy movement for skeletons
                return 1.5f * (stateTime - deltaTime);

            default:
                return 0.75f * (stateTime - deltaTime);
        }
    }

    @Override
    public void createBody(World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(8, 6);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 1;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = CollisionManager.SOLID_GROUP;
        fixtureDef.filter.maskBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);

        //Do not draw if must be removed
        if (mustRemoveFromRoom) {
            ((EchoActorEntity) entity).stopStartingSound();
            return;
        }

        if (isInvisible) {
            //Don't draw anything
            return;
        }

        batch.begin();

        // Should not loop!
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, mapStateTimeFromBehaviour(stateTime));

        batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);
        batch.end();
    }

    public boolean mustRemoveFromRoom() {
        return mustRemoveFromRoom;
    }

    /**
     * @return TextBox key to show based on current actor step
     */
    public String getCurrentTextBoxToShow() {
        if (showTextBox) {
            showTextBox = false;
        } else {
            textBoxToShow = null;
        }
        return textBoxToShow;
    }

    /**
     * Set textbox to show
     *
     * @param key
     */
    public void showTextBox(String key) {
        showTextBox = true;
        textBoxToShow = key;
    }

    /**
     *
     */
    public void setInvisible() {
        isInvisible = true;
    }

    /**
     *
     */
    public void setVisible() {
        isInvisible = false;
    }


    public boolean hasCurrentTextBoxToShow() {
        return Objects.nonNull(textBoxToShow) && showTextBox;
    }

    public void playStartingSound() {
        ((EchoActorEntity) entity).playStartingSound();
    }

    @Override
    public double damageRoll() {
        //Only certain echoes should harm the player
        switch (((EchoActorEntity) entity).getEchoesActorType()) {
            case DEAD_HAND:
            case DEAD_DOUBLE_HAND:
                //Ld6
                return Math.min(MathUtils.random(1, 6), MathUtils.random(1, 6));
            case HORROR:
            case INFERNUM:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        //If active, hurt player
        if (isEchoActive) {
            playerInstance.hurt(this);
        }
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        //Nothing to do here... yet
    }

    /**
     * @param layerToShow
     */
    public void setLayerToShow(String layerToShow) {
        this.layerToShow = layerToShow;
    }

    /**
     * @return the Map layer to draw
     */
    public String overrideMapLayerDrawn() {
        return Objects.isNull(layerToShow) ? MapLayersEnum.TERRAIN_LAYER.getLayerName() : MapLayersEnum.valueOf(layerToShow).getLayerName();
    }

    public EchoesActorType getType() {
        return ((EchoActorEntity) entity).getEchoesActorType();
    }

    public void endEcho(){
        mustRemoveFromRoom = true;
    }
}
