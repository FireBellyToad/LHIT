package com.faust.lhitgame.game.instances.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.faust.lhitgame.LHITGame;
import com.faust.lhitgame.game.echoes.enums.EchoesActorType;
import com.faust.lhitgame.game.gameentities.AnimatedEntity;
import com.faust.lhitgame.game.gameentities.impl.EscapePortalEntity;
import com.faust.lhitgame.game.instances.interfaces.Hurtable;
import com.faust.lhitgame.game.instances.interfaces.Killable;
import com.faust.lhitgame.game.instances.AnimatedInstance;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.interfaces.Interactable;
import com.faust.lhitgame.game.rooms.RoomContent;
import com.faust.lhitgame.game.world.manager.CollisionManager;

import java.util.Objects;

/**
 * @author Escape Portal Instance class
 */
public class EscapePortalInstance extends AnimatedInstance implements Interactable, Killable, Hurtable {

    private boolean isTouched = false;

    public EscapePortalInstance(AssetManager assetManager) {
        super(new EscapePortalEntity(assetManager));
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        EchoActorInstance infernumActor = roomContent.echoActors.stream().filter(e -> EchoesActorType.INFERNUM.equals(e.getType())).findAny().orElse(null);

        //Remove on infernum echo end
        if(Objects.isNull(infernumActor) || infernumActor.mustRemoveFromRoom()){
            isTouched = true;
        }
    }

    private float mapStateTimeFromBehaviour(float stateTime) {
        return stateTime;
    }

    @Override
    public void createBody(World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x, y- POSITION_OFFSET);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(4, 12);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 0;
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
        //is fixed;
        Objects.requireNonNull(batch);
        batch.begin();
        TextureRegion frame = ((AnimatedEntity) entity).getFrame(currentBehavior, mapStateTimeFromBehaviour(stateTime), true);
        batch.draw(frame, body.getPosition().x - POSITION_OFFSET, body.getPosition().y );
        batch.end();
    }

    @Override
    public boolean isDying() {
        return isTouched;
    }

    @Override
    public boolean isDead() {
        return isTouched;
    }

    @Override
    public int getResistance() {
        return 0;
    }

    @Override
    public void hurt(GameInstance attacker) {
        isTouched = true;
    }

    @Override
    public void postHurtLogic(GameInstance attacker) {
        //Nothing to do here... yet
    }

    @Override
    public void doPlayerInteraction(PlayerInstance playerInstance) {
        this.hurt(playerInstance);
    }

    @Override
    public void endPlayerInteraction(PlayerInstance playerInstance) {
        //Nothing to do here... yet
    }
}
