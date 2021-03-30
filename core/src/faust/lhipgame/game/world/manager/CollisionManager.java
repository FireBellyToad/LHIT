package faust.lhipgame.game.world.manager;

import com.badlogic.gdx.physics.box2d.*;
import faust.lhipgame.game.gameentities.Killable;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.Interactable;
import faust.lhipgame.game.instances.impl.BoundedInstance;
import faust.lhipgame.game.instances.impl.DecorationInstance;
import faust.lhipgame.game.instances.impl.PlayerInstance;
import faust.lhipgame.game.instances.impl.StrixInstance;
import faust.lhipgame.game.rooms.areas.EmergedArea;

import java.util.Objects;

/**
 * Class for handling all the collisions between GameInstances
 */
public class CollisionManager implements ContactListener {

    public static final int SOLID_GROUP = 1;
    public static final int PLAYER_GROUP = 2;
    public static final int ENEMY_GROUP = 4;
    public static final int WEAPON_GROUP = 8;

    @Override
    public void beginContact(Contact contact) {
        //FIXME
        if (isContactOfClass(contact, PlayerInstance.class)) {
            handlePlayerBeginContact(contact);
        }

    }

    @Override
    public void endContact(Contact contact) {
        //FIXME
        if (isContactOfClass(contact, PlayerInstance.class)) {
            handlePlayerEndContact(contact);
        }

    }

    /**
     * Handleing player contact start
     *
     * @param contact
     */
    private void handlePlayerBeginContact(Contact contact) {
        // Handle emerged area begin
        if (isContactOfClass(contact, EmergedArea.class)) {
            //Player emerges from water
            PlayerInstance pInst = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            pInst.setSubmerged(false);
        }


        // Handle Decoration Collision
        if (isContactOfClass(contact, DecorationInstance.class)) {
            //If decoration is passable, just do and Interaction. Else stop the player
            DecorationInstance decorationInstance = ((DecorationInstance) getCorrectFixture(contact, DecorationInstance.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            if (decorationInstance.isPassable())
                decorationInstance.doPlayerInteraction(playerInstance);
            else
                playerInstance.stopAll();
        }

        // Handle Strix Collision
        if (isContactOfClass(contact, StrixInstance.class)) {
            handleEnemyCollisionEvent(contact, StrixInstance.class, false);
        }

        // Handle Bounded Collision
        if (isContactOfClass(contact, BoundedInstance.class)) {
            handleEnemyCollisionEvent(contact, BoundedInstance.class, true);
        }
    }

    /**
     * Global handler for player and enemy instances collision
     * @param contact
     * @param enemyGameInstanceClass
     * @param halvesNormalLanceDamage true if halve normal damage
     * @param <T> an Interactable and Killable instance, usually enemy
     */
    private <T extends Interactable & Killable> void handleEnemyCollisionEvent(Contact contact, Class<T> enemyGameInstanceClass, boolean halvesNormalLanceDamage) {

        Fixture enemyInstanceFixture = getCorrectFixture(contact, enemyGameInstanceClass);
        Body enemyInstanceBody = enemyInstanceFixture.getBody();
        T enemyInstance = (T) enemyInstanceBody.getUserData();

        Body playerBody = getCorrectFixture(contact, PlayerInstance.class).getBody();
        PlayerInstance playerInstance = (PlayerInstance) playerBody.getUserData();

        if (BodyDef.BodyType.DynamicBody.equals(enemyInstanceBody.getType()) && BodyDef.BodyType.DynamicBody.equals(playerBody.getType())) {
            // Attacking player
            enemyInstance.doPlayerInteraction(playerInstance);
        } else if (BodyDef.BodyType.DynamicBody.equals(enemyInstanceBody.getType()) && BodyDef.BodyType.KinematicBody.equals(playerBody.getType())) {
            enemyInstance.hurt(playerInstance);
        }else if (BodyDef.BodyType.KinematicBody.equals(enemyInstanceBody.getType()) && BodyDef.BodyType.DynamicBody.equals(playerBody.getType())) {
            // Player Hurt
            playerInstance.hurt((GameInstance) enemyInstance);
        }
    }

    /**
     * Handling Player contact end
     *
     * @param contact
     */
    private void handlePlayerEndContact(Contact contact) {
        // Handle emerged area end
        if (isContactOfClass(contact, EmergedArea.class)) {
            //Player submerges in water
            PlayerInstance pInst = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            pInst.setSubmerged(true);
        }

        // Handle Decoration Collision end
        if (isContactOfClass(contact, DecorationInstance.class)) {
            //If decoration is passable, just end interaction
            DecorationInstance inst = ((DecorationInstance) getCorrectFixture(contact, DecorationInstance.class).getBody().getUserData());
            PlayerInstance pInst = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            if (inst.isPassable())
                inst.endPlayerInteraction(pInst);
        }

        // Handle Strix Collision end
        if (isContactOfClass(contact, StrixInstance.class)) {
            handleEnemyCollisionEventEnd(contact, StrixInstance.class);
        }

        // Handle Bounded Collision end
        if (isContactOfClass(contact, BoundedInstance.class)) {
            handleEnemyCollisionEventEnd(contact, BoundedInstance.class);
        }
    }

    /**
     * Global handler for player and enemy instances collision
     * @param contact
     * @param enemyGameInstanceClass
     * @param <T> an Interactable and Killable instance, usually enemy
     */
    private <T extends Interactable & Killable> void handleEnemyCollisionEventEnd(Contact contact,  Class<T>  enemyGameInstanceClass) {
        //Just free the player from leech grapple
        Body enemyBody = getCorrectFixture(contact,enemyGameInstanceClass).getBody();
        T sInst = (T) enemyBody.getUserData();

        Body playerBody = getCorrectFixture(contact, PlayerInstance.class).getBody();
        PlayerInstance pInst = (PlayerInstance) playerBody.getUserData();

        if (BodyDef.BodyType.DynamicBody.equals(enemyBody.getType()) && BodyDef.BodyType.DynamicBody.equals(playerBody.getType())) {
            sInst.endPlayerInteraction(pInst);
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
     *
     * @param contact
     * @param gameInstanceClass the class of the GameInstance to check
     * @param <T>               the class of the GameInstance to check
     * @return
     */
    private <T> boolean isContactOfClass(Contact contact, Class<T> gameInstanceClass) {
        Objects.requireNonNull(contact.getFixtureA().getBody().getUserData());
        Objects.requireNonNull(contact.getFixtureB().getBody().getUserData());

        return contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass) ||
                contact.getFixtureB().getBody().getUserData().getClass().equals(gameInstanceClass);
    }

    /**
     * Extract from a contact the fixture of the GameInstance
     *
     * @param contact
     * @param gameInstanceClass the class of the GameInstance fixture to extract
     * @param <T>
     * @return
     */
    private <T> Fixture getCorrectFixture(Contact contact, Class<T> gameInstanceClass) {
        if (contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass))
            return contact.getFixtureA();

        return contact.getFixtureB();
    }
}
