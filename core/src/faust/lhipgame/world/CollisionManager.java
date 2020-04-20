package faust.lhipgame.world;

import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.instances.DecorationInstance;
import faust.lhipgame.instances.PlayerInstance;

import java.util.Objects;

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
            DecorationInstance inst = ((DecorationInstance) getCorrectFixture(contact,DecorationInstance.class).getBody().getUserData());
            if(inst.isPassable())
                inst.setInteracted(true);
            else
                ((PlayerInstance) getCorrectFixture(contact,PlayerInstance.class).getBody().getUserData()).stopAll();
        }
    }

    private void handlePlayerEndContact(Contact contact) {
        if(isContactOfClass(contact, DecorationInstance.class)){
            DecorationInstance inst = ((DecorationInstance) getCorrectFixture(contact,DecorationInstance.class).getBody().getUserData());
            if(inst.isPassable())
                inst.setInteracted(false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private <T> boolean isContactOfClass(Contact contact, Class<T> gameInstanceClass){
        Objects.requireNonNull(contact.getFixtureA().getBody().getUserData());
        Objects.requireNonNull(contact.getFixtureB().getBody().getUserData());

        return contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass) ||
                contact.getFixtureB().getBody().getUserData().getClass().equals(gameInstanceClass) ;
    }


    private <T> Fixture getCorrectFixture(Contact contact,Class<T> gameInstanceClass) {
        if(contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass))
            return contact.getFixtureA();

        return contact.getFixtureB();

    }
}
