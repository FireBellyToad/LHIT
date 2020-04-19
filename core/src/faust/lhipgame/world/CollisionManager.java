package faust.lhipgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.instances.PlayerInstance;

public class CollisionManager implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA.getBody().getUserData() instanceof PlayerInstance){
            ((PlayerInstance) fixtureA.getBody().getUserData()).stopAll();
        }
        if(fixtureB.getBody().getUserData() instanceof PlayerInstance){
            ((PlayerInstance) fixtureB.getBody().getUserData()).stopAll();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
