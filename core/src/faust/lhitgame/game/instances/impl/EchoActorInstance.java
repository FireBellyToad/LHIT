package faust.lhitgame.game.instances.impl;

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
import faust.lhitgame.game.echoes.enums.EchoesActorType;
import faust.lhitgame.game.gameentities.AnimatedEntity;
import faust.lhitgame.game.gameentities.enums.DirectionEnum;
import faust.lhitgame.game.gameentities.enums.GameBehavior;
import faust.lhitgame.game.gameentities.impl.EchoActorEntity;
import faust.lhitgame.game.gameentities.interfaces.Damager;
import faust.lhitgame.game.gameentities.interfaces.Killable;
import faust.lhitgame.game.instances.AnimatedInstance;
import faust.lhitgame.game.instances.Spawner;
import faust.lhitgame.game.instances.interfaces.Interactable;
import faust.lhitgame.game.rooms.AbstractRoom;
import faust.lhitgame.game.world.manager.CollisionManager;

import java.util.List;
import java.util.Objects;


/**
 * Class for Echo Actors Instances
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorInstance extends AnimatedInstance implements Interactable, Damager {

    private final EchoesActorType echoesActorType;
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
        this.echoesActorType = echoesActorType;

        //get first step
        this.currentBehavior = ((EchoActorEntity)this.entity).getStepOrder().get(0);
    }

    @Override
    public void doLogic(float stateTime, AbstractRoom currentRoom) {

        // If must be removed, avoid logic
        if(removeFromRoom){
            return;
        }

        //Check if echo is active. On first iteration set to true
        if(!echoIsActive){
            echoIsActive = true;
        }

        //initialize deltatime
        if (deltaTime == 0)
            deltaTime = stateTime;

        //Move if should
        if(((EchoActorEntity)this.entity).mustMoveInStep(currentBehavior)){
            body.setLinearVelocity(getVelocityOfStep(((EchoActorEntity)this.entity).getDirection(currentBehavior)));
        } else {
            body.setLinearVelocity(0,0);
        }

        //If animation is finished pass to the next step
       if (((EchoActorEntity)this.entity).isAnimationFinished(currentBehavior,mapStateTimeFromBehaviour(stateTime))){
           final List<GameBehavior> stepOrder = ((EchoActorEntity) entity).getStepOrder();
           int index = getNewIndex(stepOrder, currentRoom);

           Gdx.app.log("DEBUG","Echo Actor "+ ((EchoActorEntity) entity).getEchoesActorType() + " end step "+ currentBehavior);

           deltaTime = stateTime;
           //If is not last step
           if(index+1 < stepOrder.size()){
               currentBehavior = ((EchoActorEntity) entity).getStepOrder().get(index+1);
               showTextBox = true;
           } else {
               removeFromRoom = true;
               spawnInstancesOnEnd();
               Gdx.app.log("DEBUG","Echo Actor "+ ((EchoActorEntity) entity).getEchoesActorType() + " must be removed ");
           }
       }
    }

    /**
     * New step from logics
     * @param stepOrder
     * @param currentRoom
     * @return
     */
    private int getNewIndex(List<GameBehavior> stepOrder, AbstractRoom currentRoom) {

        //If has "go to step", handle it correctly
        if(Objects.nonNull(((EchoActorEntity) entity).getGotoToStepFromStep(currentBehavior))){

            //Check condition on until there is at least one enemy of type is alive in room
            if(Objects.nonNull(((EchoActorEntity) entity).getUntilAtLeastOneFromStep(currentBehavior))) {
                //Extract instance class from enum and do check
                Class<? extends AnimatedInstance> enemyClass = ((EchoActorEntity) entity).getUntilAtLeastOneFromStep(currentBehavior).getInstanceClass();
                if(currentRoom.getEnemyList().stream().anyMatch(e -> enemyClass.equals(e.getClass()) && !((Killable)e).isDead())){
                    //if true, go to step
                    return stepOrder.indexOf(((EchoActorEntity) entity).getGotoToStepFromStep(currentBehavior));
                }
            } else {
                //If no "until" condition, just jump to "go to step" value
                return stepOrder.indexOf(((EchoActorEntity) entity).getGotoToStepFromStep(currentBehavior));
            }

        }

        // or else, just get next
        return stepOrder.indexOf(currentBehavior);
    }

    /**
     * Spawn instance if doable
     */
    private void spawnInstancesOnEnd() {
        //FIXME do by script not echotype
        switch (echoesActorType){
            case VICTIM: {
                spawner.spawnInstance(POIInstance.class,startX,startY+8);
                break;
            }
            case HORROR_BODY:{
                spawner.spawnInstance(WillowispInstance.class,startX,startY+8);
                break;
            }
        }
    }

    /**
     *
     * @param direction
     * @return velocity of movement to do in Vector2 format
     */
    private Vector2 getVelocityOfStep(DirectionEnum direction) {
        final Integer speedInCurrentStep = ((EchoActorEntity) this.entity).getSpeedInStep(currentBehavior);
        switch (direction){
            case UP:
                return new Vector2(0, speedInCurrentStep);
            case DOWN:
                return new Vector2(0,-speedInCurrentStep);
            case RIGHT:
                return new Vector2(speedInCurrentStep,0);
            case LEFT:
                return new Vector2(-speedInCurrentStep,0);
        }
        return null;
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {
        switch (((EchoActorEntity) entity).getEchoesActorType()){
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
        fixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;
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
        if(removeFromRoom){
            ((EchoActorEntity) entity).stopStartingSound();
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
     *
     * @return TextBox key to show based on current actor step
     */
    public String getCurrentTextBoxToShow(){
        if(showTextBox){
            showTextBox = false;
            return ((EchoActorEntity) entity).getTextBoxPerStep(currentBehavior);
        }

        return null;
    }

    public boolean hasCurrentTextBoxToShow() {
        return Objects.nonNull(((EchoActorEntity) entity).getTextBoxPerStep(currentBehavior)) && showTextBox;
    }

    public void playStartingSound() {
        ((EchoActorEntity) entity).playStartingSound();
    }

    @Override
    public double damageRoll() {
        //Only certain echoes should harm the player
        switch (((EchoActorEntity) entity).getEchoesActorType()){
            case DEAD_HAND:
            case DEAD_DOUBLE_HAND:
                //Ld6
                return Math.min(MathUtils.random(1,6),MathUtils.random(1,6));
            case HORROR:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        //If active, hurt player
        if(echoIsActive){
            playerInstance.hurt(this);
        }
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        //Nothing to do here... yet
    }
}
