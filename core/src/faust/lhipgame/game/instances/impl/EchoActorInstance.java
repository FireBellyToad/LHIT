package faust.lhipgame.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.game.echoes.enums.EchoesActorType;
import faust.lhipgame.game.gameentities.AnimatedEntity;
import faust.lhipgame.game.gameentities.Attacker;
import faust.lhipgame.game.gameentities.enums.GameBehavior;
import faust.lhipgame.game.gameentities.impl.EchoActorEntity;
import faust.lhipgame.game.instances.AnimatedInstance;
import faust.lhipgame.game.instances.Interactable;
import faust.lhipgame.game.world.manager.CollisionManager;

import java.lang.annotation.Inherited;
import java.util.List;
import java.util.Objects;


/**
 * Class for Echo Actors Instances
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorInstance extends AnimatedInstance implements Interactable, Attacker {

    private boolean removeFromRoom = false;
    private boolean showTextBox = true;
    private float deltaTime = 0; // Time delta between step start and current
    private boolean echoIsActive;

    public EchoActorInstance(EchoesActorType echoesActorType, float x, float y, AssetManager assetManager) {
        super(new EchoActorEntity(echoesActorType, assetManager));
        this.startX = x;
        this.startY = y;
        this.echoIsActive = false;

        //get first step
        this.currentBehavior = ((EchoActorEntity)this.entity).getStepOrder().get(0);
    }

    @Override
    public void doLogic(float stateTime) {

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
            //TODO improve
            body.setLinearVelocity(60,0);
        } else {
            body.setLinearVelocity(0,0);
        }

        //If animation is finished pass to the next step
       if (((EchoActorEntity)this.entity).isAnimationFinished(currentBehavior,mapStateTimeFromBehaviour(stateTime))){
           final List<GameBehavior> stepOrder = ((EchoActorEntity) entity).getStepOrder();
           int index = stepOrder.indexOf(currentBehavior);

           //If has "go to step", handle it correctly
           if(Objects.nonNull(((EchoActorEntity) entity).getGotoToStepFromStep(currentBehavior))){
               index = stepOrder.indexOf(((EchoActorEntity) entity).getGotoToStepFromStep(currentBehavior));
           }

           Gdx.app.log("DEBUG","Echo Actor "+ ((EchoActorEntity) entity).getEchoesActorType() + " end step "+ currentBehavior);

           deltaTime = stateTime;
           //If is not last step
           if(index+1 < stepOrder.size()){
               currentBehavior = ((EchoActorEntity) entity).getStepOrder().get(index+1);
               showTextBox = true;
           } else {
               removeFromRoom = true;
               Gdx.app.log("DEBUG","Echo Actor "+ ((EchoActorEntity) entity).getEchoesActorType() + " must be removed ");
           }
       }
    }

    @Override
    protected float mapStateTimeFromBehaviour(float stateTime) {
        return 0.75f * (stateTime - deltaTime);
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
                return Math.min(MathUtils.random(1,6),MathUtils.random(1,6));
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
