package faust.lhipgame.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import faust.lhipgame.echoes.enums.EchoesActorType;
import faust.lhipgame.gameentities.AnimatedEntity;
import faust.lhipgame.gameentities.enums.Direction;
import faust.lhipgame.gameentities.enums.GameBehavior;
import faust.lhipgame.gameentities.impl.EchoActorEntity;
import faust.lhipgame.instances.AnimatedInstance;

import java.util.List;
import java.util.Objects;


/**
 * Class for Echo Actors Instances
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class EchoActorInstance extends AnimatedInstance {

    private boolean removeFromRoom = false;
    private float deltaTime = 0; // Time delta between step start and current

    public EchoActorInstance(EchoesActorType echoesActorType, float x, float y) {
        super(new EchoActorEntity(echoesActorType));
        this.startX = x;
        this.startY = y;

        //get first step
        this.currentBehavior = ((EchoActorEntity)this.entity).getStepOrder().get(0);

    }

    @Override
    public void doLogic(float stateTime) {

        // If must be removed, avoid logic
        if(removeFromRoom){
            return;
        }

        //initialize deltatime
        if (deltaTime == 0)
            deltaTime = stateTime;

        //If animation is finished pass to the next step
       if (((EchoActorEntity)this.entity).isAnimationFinished(currentBehavior,stateTime - deltaTime)){
           final List<GameBehavior> stepOrder = ((EchoActorEntity) entity).getStepOrder();
           final int index = stepOrder.indexOf(currentBehavior);
           Gdx.app.log("DEBUG","Echo Actor "+ ((EchoActorEntity) entity).getEchoesActorType() + " end step "+ currentBehavior);

           //If is not last step
           if(index+1 < stepOrder.size()){
               currentBehavior = ((EchoActorEntity) entity).getStepOrder().get(index+1);
               deltaTime = 0;
           } else {
               removeFromRoom = true;
               Gdx.app.log("DEBUG","Echo Actor "+ ((EchoActorEntity) entity).getEchoesActorType() + " must be removed ");
           }
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

        if(removeFromRoom){
            return;
        }

        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, Direction.UNUSED,stateTime - deltaTime, false);

        batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y - POSITION_Y_OFFSET);

    }

    public boolean mustRemoveFromRoom() {
        return removeFromRoom;
    }


    public EchoesActorType getEchoesActorType() {
        return ((EchoActorEntity) entity).getEchoesActorType();
    }
}
