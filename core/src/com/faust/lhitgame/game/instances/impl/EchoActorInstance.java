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

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Class for Echo Actors Instances
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorInstance extends AnimatedInstance implements Interactable, Damager {

    private boolean removeFromRoom = false;
    private boolean showTextBox = true;
    private float deltaTime = 0; // Time delta between step start and current
    private boolean echoIsActive;
    private final Spawner spawner;


    public EchoActorInstance(EchoesActorType echoesActorType, float x, float y, AssetManager assetManager, Spawner spawner) {
        super(new EchoActorEntity(echoesActorType, assetManager));
        this.startX = x;
        this.startY = y;
        this.echoIsActive = false;
        this.spawner = spawner;

        //get first step
        this.currentBehavior = ((EchoActorEntity) this.entity).getStepOrder().get(0);
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        // If must be removed, avoid logic
        if (!removeFromRoom) {

            //Check if echo is active. On first iteration set to true
            if (!echoIsActive) {
                echoIsActive = true;
            }

            //initialize deltatime
            if (deltaTime == 0)
                deltaTime = stateTime;

            //All commands to do in this step
            executeCommands(((EchoActorEntity) this.entity).getCommandsForStep(currentBehavior), roomContent, stateTime);
        }
    }

    /**
     * Executes all the commands in one step
     *
     * @param commands    the commands - values Map
     * @param roomContent the room contents
     * @param stateTime   stateTime of the main loop
     */
    private void executeCommands(final Map<EchoCommandsEnum, Object> commands, RoomContent roomContent, float stateTime) {
        //Move if should
        if (commands.containsKey(EchoCommandsEnum.DIRECTION) && commands.containsKey(EchoCommandsEnum.SPEED)) {
            DirectionEnum directionEnum = DirectionEnum.valueOf((String) commands.get(EchoCommandsEnum.DIRECTION));
            float speed = (Integer) commands.get(EchoCommandsEnum.SPEED);
            body.setLinearVelocity(getVelocityOfStep(directionEnum, speed));
        } else {
            body.setLinearVelocity(0, 0);
        }

        //If animation is finished pass to the next step
        if (((EchoActorEntity) this.entity).isAnimationFinished(currentBehavior, mapStateTimeFromBehaviour(stateTime))) {
            final List<GameBehavior> stepOrder = ((EchoActorEntity) entity).getStepOrder();
            int index = getNewIndex(stepOrder, commands, roomContent);

            Gdx.app.log("DEBUG", "Echo Actor " + ((EchoActorEntity) entity).getEchoesActorType() + " end step " + currentBehavior);

            deltaTime = stateTime;
            //If is not last step
            if (index + 1 < stepOrder.size()) {
                //Before stepping out, hurt player and check if canKill player
                if (commands.containsKey(EchoCommandsEnum.DAMAGE) &&
                        ((Boolean) commands.getOrDefault(EchoCommandsEnum.CAN_KILL_PLAYER, true) || !roomContent.player.isDying())) {
                    roomContent.player.hurt(this);
                }

                currentBehavior = ((EchoActorEntity) entity).getStepOrder().get(index + 1);
                showTextBox = true;
            } else {
                removeFromRoom = true;
                spawnInstancesOnEnd(commands);
                Gdx.app.log("DEBUG", "Echo Actor " + ((EchoActorEntity) entity).getEchoesActorType() + " must be removed ");
            }
        }
    }

    /**
     * New step from logics
     *
     * @param stepOrder
     * @param commands
     * @param roomContent
     * @return
     */
    private int getNewIndex(List<GameBehavior> stepOrder, Map<EchoCommandsEnum, Object> commands, RoomContent roomContent) {

        //If has "go to step", handle it correctly
        if (commands.containsKey(EchoCommandsEnum.STEP)) {

            final Integer index = (Integer) commands.get(EchoCommandsEnum.STEP);

            if (checkConditionalCommands(commands,roomContent)) {
                //If no "until" condition, just jump to "go to step" value
                return stepOrder.indexOf(GameBehavior.getFromOrdinal(index));
            }

        }

        // or else, just get next
        return stepOrder.indexOf(currentBehavior);
    }

    /**
     *
     * @param commands
     * @param roomContent
     * @return true if all conditionals commands are true
     */
    private boolean checkConditionalCommands(Map<EchoCommandsEnum, Object> commands, RoomContent roomContent) {
        boolean areConditionsTrue = true;
        //Check condition on until Player has at least less then N damage (priority on other checks)
        if (commands.containsKey(EchoCommandsEnum.IF_PLAYER_DAMAGE_IS_LESS_THAN)) {
            //Extract value of damage
            final int value = (int) commands.get(EchoCommandsEnum.IF_PLAYER_DAMAGE_IS_LESS_THAN);
            areConditionsTrue = roomContent.player.getDamage() <= value;
        } else if (commands.containsKey(EchoCommandsEnum.IF_PLAYER_DAMAGE_IS_MORE_THAN)) {
            //Check condition on if Player has more then N damage (priority on other checks)
            final int value = (int) commands.get(EchoCommandsEnum.IF_PLAYER_DAMAGE_IS_MORE_THAN);
            areConditionsTrue = roomContent.player.getDamage() > value;
        }

        //Check condition on until there is at least one enemy of type is alive in room
        if (commands.containsKey(EchoCommandsEnum.IF_AT_LEAST_ONE_KILLABLE_ALIVE)) {
            //Extract instance class from enum and do check
            final EnemyEnum enemyEnum = EnemyEnum.valueOf((String) commands.get(EchoCommandsEnum.IF_AT_LEAST_ONE_KILLABLE_ALIVE));
            final Class<? extends AnimatedInstance> enemyClass = enemyEnum.getInstanceClass();
            areConditionsTrue = areConditionsTrue && roomContent.enemyList.stream().anyMatch(e -> enemyClass.equals(e.getClass()) && !((Killable) e).isDead());
        } else if (commands.containsKey(EchoCommandsEnum.IF_NO_KILLABLE_ALIVE)) {
            //Check condition on if there is no enemy of type is alive in room
            //Extract instance class from enum and do check
            final EnemyEnum enemyEnum = EnemyEnum.valueOf((String) commands.get(EchoCommandsEnum.IF_NO_KILLABLE_ALIVE));
            final Class<? extends AnimatedInstance> enemyClass = enemyEnum.getInstanceClass();
            areConditionsTrue = areConditionsTrue && !roomContent.enemyList.stream().anyMatch(e -> enemyClass.equals(e.getClass()) && !((Killable) e).isDead());
        }

        if (commands.containsKey(EchoCommandsEnum.IF_AT_LEAST_ONE_POI_EXAMINABLE)) {
            //Extract Poi type and do check
            final POIEnum poiEnum = POIEnum.valueOf((String) commands.get(EchoCommandsEnum.IF_AT_LEAST_ONE_POI_EXAMINABLE));
            areConditionsTrue = areConditionsTrue && roomContent.poiList.stream().anyMatch(poi -> poiEnum.equals(poi.getType()) && poi.isAlreadyExamined());
        }

        return areConditionsTrue;
    }

    /**
     * Spawn instance if doable
     *
     * @param commands
     */
    private void spawnInstancesOnEnd(Map<EchoCommandsEnum, Object> commands) {

        //Should not spawn anything if has no identifier
        if (!commands.containsKey(EchoCommandsEnum.IDENTIFIER))
            return;

        //FIXME use class name?
        String thingName = (String) commands.get(EchoCommandsEnum.IDENTIFIER);
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
        boolean useRelative = (boolean) commands.getOrDefault(EchoCommandsEnum.RELATIVE, false);

        if (commands.containsKey(EchoCommandsEnum.X)) {
            final int value = (int) commands.get(EchoCommandsEnum.X);
            spawnX = useRelative ? spawnX + value : value;
        }
        if (commands.containsKey(EchoCommandsEnum.Y)) {
            final int value = (int) commands.get(EchoCommandsEnum.Y);
            spawnY = useRelative ? spawnY + value : value;
        }

        if (Objects.nonNull(enemyEnum)) {
            spawner.spawnInstance(enemyEnum.getInstanceClass(), spawnX, spawnY, enemyEnum.name());
        } else if (Objects.nonNull(poiEnum)) {
            spawner.spawnInstance(POIInstance.class, spawnX, spawnY, poiEnum.name());
        }
    }

    /**
     * @param direction
     * @param speedInCurrentStep
     * @return velocity of movement to do in Vector2 format
     */
    private Vector2 getVelocityOfStep(DirectionEnum direction, float speedInCurrentStep) {
        switch (direction) {
            case UP:
                return new Vector2(0, speedInCurrentStep);
            case DOWN:
                return new Vector2(0, -speedInCurrentStep);
            case RIGHT:
                return new Vector2(speedInCurrentStep, 0);
            case LEFT:
                return new Vector2(-speedInCurrentStep, 0);
        }
        return null;
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
        if (removeFromRoom) {
            ((EchoActorEntity) entity).stopStartingSound();
            return;
        }

        final Map<EchoCommandsEnum, Object> commands = ((EchoActorEntity) this.entity).getCommandsForStep(currentBehavior);
        if ((Boolean) commands.getOrDefault(EchoCommandsEnum.INVISIBLE, false)) {
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
        return removeFromRoom;
    }

    /**
     * @return TextBox key to show based on current actor step
     */
    public String getCurrentTextBoxToShow() {
        if (showTextBox) {
            showTextBox = false;
            Map<EchoCommandsEnum, Object> commandOnCurrentStep = ((EchoActorEntity) entity).getCommandsForStep(currentBehavior);
            return (String) commandOnCurrentStep.get(EchoCommandsEnum.TEXTBOX_KEY);
        }

        return null;
    }

    public boolean hasCurrentTextBoxToShow() {
        return ((EchoActorEntity) entity).getCommandsForStep(currentBehavior).containsKey(EchoCommandsEnum.TEXTBOX_KEY) && showTextBox;
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
        if (echoIsActive) {
            playerInstance.hurt(this);
        }
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        //Nothing to do here... yet
    }

    /**
     * @return the Map layer to draw
     */
    public String overrideMapLayerDrawn() {
        final Map<EchoCommandsEnum, Object> extractedCommand = ((EchoActorEntity) this.entity).getCommandsForStep(currentBehavior);
        final String layerString = (String) extractedCommand.get(EchoCommandsEnum.RENDER_ONLY_MAP_LAYER);
        return Objects.isNull(layerString) ? MapLayersEnum.TERRAIN_LAYER.getLayerName() : MapLayersEnum.valueOf(layerString).getLayerName();
    }

    public EchoesActorType getType() {
        return ((EchoActorEntity) entity).getEchoesActorType();
    }
}
