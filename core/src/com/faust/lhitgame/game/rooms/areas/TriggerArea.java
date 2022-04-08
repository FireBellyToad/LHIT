package com.faust.lhitgame.game.rooms.areas;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.faust.lhitgame.game.gameentities.enums.ItemEnum;
import com.faust.lhitgame.game.rooms.enums.TriggerTypeEnum;

import java.util.List;
import java.util.Objects;

/**
 * Trigger area.
 * @author Jacopo "Faust" Buttiglieri
 */
public class TriggerArea {

    private final long triggerId;
    private final TriggerTypeEnum triggerTypeEnum;
    private final List<ItemEnum> itemsNeededForTrigger;

    private Body body;
    private final Rectangle triggerRect;

    public TriggerArea(long triggerId, TriggerTypeEnum triggerTypeEnum, List<ItemEnum> itemsNeededForTrigger, Rectangle wallRect) {
        this.triggerId = triggerId;
        this.triggerTypeEnum = triggerTypeEnum;
        this.itemsNeededForTrigger = itemsNeededForTrigger;
        this.triggerRect = wallRect;
    }

    public void dispose() {
        this.body.getFixtureList().forEach(f ->
                this.body.destroyFixture(f));
    }

    public void createBody(final World world) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.fixedRotation = true;

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(triggerRect.width / 2, triggerRect.height / 2);
        bodyDef.position.set(triggerRect.x + triggerRect.width / 2, triggerRect.y + triggerRect.height / 2);

        // Define Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density =  1;
        fixtureDef.friction =  1;
        fixtureDef.isSensor = false;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    public long getTriggerId() {
        return triggerId;
    }

    public TriggerTypeEnum getTriggerTypeEnum() {
        return triggerTypeEnum;
    }

    public List<ItemEnum> getItemsNeededForTrigger() {
        return itemsNeededForTrigger;
    }

    public Body getBody() {
        return body;
    }

    public Rectangle getTriggerRect() {
        return triggerRect;
    }
}
