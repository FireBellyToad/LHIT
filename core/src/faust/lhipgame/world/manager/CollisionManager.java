package faust.lhipgame.world.manager;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.instances.impl.DecorationInstance;
import faust.lhipgame.instances.impl.PlayerInstance;
import faust.lhipgame.instances.impl.StrixInstance;

import java.util.Objects;

/**
 * Class for handling all the collisions between GameInstances
 */
public class CollisionManager implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        //FIXME
        if(isContactOfClass(contact,PlayerInstance.class)){
            handlePlayerBeginContact(contact);
        }

    }

    @Override
    public void endContact(Contact contact) {
        //FIXME
        if(isContactOfClass(contact,PlayerInstance.class)){
            handlePlayerEndContact(contact);
        }

    }


    private void handlePlayerBeginContact(Contact contact) {

        // Handle Decoration Collision
        if(isContactOfClass(contact, DecorationInstance.class)){
            //If decoration is passable, just do and Interaction. Else stop the player
            DecorationInstance inst = ((DecorationInstance) getCorrectFixture(contact,DecorationInstance.class).getBody().getUserData());
            PlayerInstance pInst = ((PlayerInstance) getCorrectFixture(contact,PlayerInstance.class).getBody().getUserData());
            if(inst.isPassable())
                inst.doPlayerInteraction(pInst);
            else
                pInst.stopAll();
        }

        // Handle Strix Collision
        if(isContactOfClass(contact, StrixInstance.class)){
            Body sBody = getCorrectFixture(contact,StrixInstance.class).getBody();
            StrixInstance sInst = (StrixInstance) sBody.getUserData();

            Body pBody =  getCorrectFixture(contact,PlayerInstance.class).getBody();
            PlayerInstance pInst = (PlayerInstance) pBody.getUserData();

            if(BodyDef.BodyType.DynamicBody.equals(sBody.getType()) && BodyDef.BodyType.DynamicBody.equals(pBody.getType())){
                sInst.doPlayerInteraction(pInst);
            } else if(BodyDef.BodyType.DynamicBody.equals(sBody.getType()) && BodyDef.BodyType.KinematicBody.equals(pBody.getType())){
                sInst.hurt(Math.max(MathUtils.random(1,6),MathUtils.random(1,6))+1);
            }

        }
    }

    private void handlePlayerEndContact(Contact contact) {
        // Handle Decoration Collision end
        if(isContactOfClass(contact, DecorationInstance.class)){
            //If decoration is passable, just end interaction
            DecorationInstance inst = ((DecorationInstance) getCorrectFixture(contact,DecorationInstance.class).getBody().getUserData());
            PlayerInstance pInst = ((PlayerInstance) getCorrectFixture(contact,PlayerInstance.class).getBody().getUserData());
            if(inst.isPassable())
                inst.endPlayerInteraction(pInst);
        }

        // Handle Strix Collision end
        if(isContactOfClass(contact, StrixInstance.class)){
            Body sBody = getCorrectFixture(contact,StrixInstance.class).getBody();
            StrixInstance sInst = (StrixInstance) sBody.getUserData();

            Body pBody =  getCorrectFixture(contact,PlayerInstance.class).getBody();
            PlayerInstance pInst = (PlayerInstance) pBody.getUserData();

            if(BodyDef.BodyType.DynamicBody.equals(sBody.getType())  && BodyDef.BodyType.DynamicBody.equals(pBody.getType())){
                sInst.endPlayerInteraction(pInst);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Check if this contact is of a particular Instance
     * @param contact
     * @param gameInstanceClass the class of the GameInstance to check
     * @param <T> the class of the GameInstance to check
     * @return
     */
    private <T> boolean isContactOfClass(Contact contact, Class<T> gameInstanceClass){
        Objects.requireNonNull(contact.getFixtureA().getBody().getUserData());
        Objects.requireNonNull(contact.getFixtureB().getBody().getUserData());

        return contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass) ||
                contact.getFixtureB().getBody().getUserData().getClass().equals(gameInstanceClass) ;
    }

    /**
     * Extract from a contact the fixture of the GameInstance
     * @param contact
     * @param gameInstanceClass the class of the GameInstance fixture to extract
     * @param <T>
     * @return
     */
    private <T> Fixture getCorrectFixture(Contact contact,Class<T> gameInstanceClass) {
        if(contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass))
            return contact.getFixtureA();

        return contact.getFixtureB();

    }
}
