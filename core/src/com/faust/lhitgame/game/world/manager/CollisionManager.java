package com.faust.lhitgame.game.world.manager;

import com.badlogic.gdx.physics.box2d.*;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.impl.*;
import com.faust.lhitgame.game.instances.interfaces.Hurtable;
import com.faust.lhitgame.game.instances.interfaces.Interactable;
import com.faust.lhitgame.game.rooms.areas.EmergedArea;
import com.faust.lhitgame.game.rooms.areas.TriggerArea;
import com.faust.lhitgame.game.rooms.areas.WallArea;

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

        if (isContactOfClass(contact, BoundedInstance.class)) {
            if (isContactOfClass(contact, WallArea.class) || isContactOfClass(contact, DecorationInstance.class)) {
                BoundedInstance bInst = ((BoundedInstance) getCorrectFixture(contact, BoundedInstance.class).getBody().getUserData());
                bInst.forceRecalculation();
            }
        }

        if (isContactOfClass(contact, WillowispInstance.class)) {
            if (isContactOfClass(contact, WallArea.class) || isContactOfClass(contact, DecorationInstance.class)) {
                WillowispInstance wInst = ((WillowispInstance) getCorrectFixture(contact, WillowispInstance.class).getBody().getUserData());
                wInst.forceRecalculation();
            }
        }

        if (isContactOfClass(contact, DiaconusInstance.class)) {
            if (isContactOfClass(contact, EmergedArea.class)) {
                //Diaconus emerges from water
                DiaconusInstance pInst = ((DiaconusInstance) getCorrectFixture(contact, DiaconusInstance.class).getBody().getUserData());
                pInst.setSubmerged(false);
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        //FIXME
        if (isContactOfClass(contact, PlayerInstance.class)) {
            handlePlayerEndContact(contact);
        }

        if (isContactOfClass(contact, DiaconusInstance.class)) {
            if (isContactOfClass(contact, EmergedArea.class)) {
                //Diaconus emerges from water
                DiaconusInstance pInst = ((DiaconusInstance) getCorrectFixture(contact, DiaconusInstance.class).getBody().getUserData());
                pInst.setSubmerged(true);
            }
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
            handleEnemyCollisionEvent(contact, StrixInstance.class);
        }

        // Handle Bounded Collision
        if (isContactOfClass(contact, BoundedInstance.class)) {
            handleEnemyCollisionEvent(contact, BoundedInstance.class);
        }

        // Handle Hive Collision
        if (isContactOfClass(contact, HiveInstance.class)) {
            handleEnemyCollisionEvent(contact, HiveInstance.class);
        }

        // Handle Dead hand Collision
        if (isContactOfClass(contact, EchoActorInstance.class)) {
            EchoActorInstance echoActorInstance = ((EchoActorInstance) getCorrectFixture(contact, EchoActorInstance.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            echoActorInstance.doPlayerInteraction(playerInstance);
        }

        // Handle Spitter Collision end
        if (isContactOfClass(contact, SpitterInstance.class)) {
            handleEnemyCollisionEvent(contact, SpitterInstance.class);
        }

        // Handle Willowisp Collision end
        if (isContactOfClass(contact, WillowispInstance.class)) {
            handleEnemyCollisionEvent(contact, WillowispInstance.class);
        }

        // Handle Meat Collision
        if (isContactOfClass(contact, MeatInstance.class)) {
            MeatInstance meatInstance = ((MeatInstance) getCorrectFixture(contact, MeatInstance.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            meatInstance.doPlayerInteraction(playerInstance);
        }

        // Handle Portal Collision
        if (isContactOfClass(contact, EscapePortalInstance.class)) {
            handleEnemyCollisionEvent(contact, EscapePortalInstance.class);
        }

        // Handle Diaconus Collision
        if (isContactOfClass(contact, DiaconusInstance.class)) {
            handleEnemyCollisionEvent(contact, DiaconusInstance.class);
        }

        // Handle HurtSpell Collision
        if (isContactOfClass(contact, HurtSpellInstance.class)) {
            HurtSpellInstance hurtSpellInstance = ((HurtSpellInstance) getCorrectFixture(contact, HurtSpellInstance.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            hurtSpellInstance.doPlayerInteraction(playerInstance);
        }

        // Handle ConfusionSpell Collision
        if (isContactOfClass(contact, ConfusionSpellInstance.class)) {
            ConfusionSpellInstance confusionSpellInstance = ((ConfusionSpellInstance) getCorrectFixture(contact, ConfusionSpellInstance.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            confusionSpellInstance.doPlayerInteraction(playerInstance);
        }

        // Handle TriggerArea Collision
        if (isContactOfClass(contact, TriggerArea.class)) {
            TriggerArea tInst = ((TriggerArea) getCorrectFixture(contact, TriggerArea.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());
            tInst.activate(playerInstance);
        }
    }

    /**
     * Global handler for player and enemy instances collision
     *
     * @param contact
     * @param enemyGameInstanceClass
     * @param <T>                    an Interactable and Killable instance, usually enemy
     */
    @SuppressWarnings("unchecked")
    private <T extends Interactable & Hurtable> void handleEnemyCollisionEvent(Contact contact, Class<T> enemyGameInstanceClass) {

        //Get enemy data
        Fixture enemyInstanceFixture = getCorrectFixture(contact, enemyGameInstanceClass);
        Body enemyInstanceBody = enemyInstanceFixture.getBody();
        T enemyInstance = (T) enemyInstanceBody.getUserData();

        //Get player data
        Body playerBody = getCorrectFixture(contact, PlayerInstance.class).getBody();
        PlayerInstance playerInstance = (PlayerInstance) playerBody.getUserData();

        if (BodyDef.BodyType.DynamicBody.equals(enemyInstanceBody.getType()) && BodyDef.BodyType.DynamicBody.equals(playerBody.getType())) {
            // Colliding with player,
            enemyInstance.doPlayerInteraction(playerInstance);
        } else if (BodyDef.BodyType.DynamicBody.equals(enemyInstanceBody.getType()) && BodyDef.BodyType.KinematicBody.equals(playerBody.getType())) {
            // Enemy hurt by player
            enemyInstance.hurt(playerInstance);
        } else if (BodyDef.BodyType.KinematicBody.equals(enemyInstanceBody.getType()) && BodyDef.BodyType.DynamicBody.equals(playerBody.getType())) {
            // Player hurt by enemy
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

        // Handle Hive Collision end
        if (isContactOfClass(contact, HiveInstance.class)) {
            handleEnemyCollisionEventEnd(contact, HiveInstance.class);
        }

        // Handle Spitter Collision end
        if (isContactOfClass(contact, SpitterInstance.class)) {
            handleEnemyCollisionEventEnd(contact, SpitterInstance.class);
        }
        // Handle Willowisp Collision end
        if (isContactOfClass(contact, WillowispInstance.class)) {
            handleEnemyCollisionEventEnd(contact, WillowispInstance.class);
        }
        // Handle DiaconusInstance Collision end
        if (isContactOfClass(contact, DiaconusInstance.class)) {
            handleEnemyCollisionEventEnd(contact, DiaconusInstance.class);
        }
    }

    /**
     * Global handler for player and enemy instances collision
     *
     * @param contact
     * @param enemyGameInstanceClass
     * @param <T>                    an Interactable and Hurtable instance, usually enemy
     */
    @SuppressWarnings("unchecked")
    private <T extends Interactable & Hurtable> void handleEnemyCollisionEventEnd(Contact contact, Class<T> enemyGameInstanceClass) {
        //Just free the player from leech grapple
        Body enemyBody = getCorrectFixture(contact, enemyGameInstanceClass).getBody();
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
