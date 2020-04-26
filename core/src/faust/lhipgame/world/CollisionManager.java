package faust.lhipgame.world;

import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.instances.DecorationInstance;
import faust.lhipgame.instances.PlayerInstance;

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
            if(inst.isPassable())
                inst.doPlayerInteraction();
            else
                ((PlayerInstance) getCorrectFixture(contact,PlayerInstance.class).getBody().getUserData()).stopAll();
        }
    }

    private void handlePlayerEndContact(Contact contact) {
        // Handle Decoration Collision
        if(isContactOfClass(contact, DecorationInstance.class)){
            //If decoration is passable, just end interaction
            DecorationInstance inst = ((DecorationInstance) getCorrectFixture(contact,DecorationInstance.class).getBody().getUserData());
            if(inst.isPassable())
                inst.endPlayerInteraction();
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