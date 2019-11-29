package faust.lhipgame.instances;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import faust.lhipgame.gameentities.GameEntity;

/**
 * Entity instanced in game world class
 * @author Jacopo "Faust" Buttiglieri
 */
public class GameInstance {

    private GameEntity entity;
    private BodyDef bodyDef;

    //TODO parametrize
    public GameInstance(GameEntity entity) {
        this.entity = entity;
        initBody();
    }

    /**
     * Inits the BodyDefinition
     */
    private void initBody() {
        this.bodyDef = new BodyDef();
        this.bodyDef.type = BodyDef.BodyType.KinematicBody;
        this.bodyDef.position.set(150,180);
    }

    /**
     * Draw the Entity using Body position
     * @param batch
     */
    public void draw(SpriteBatch batch){
        batch.draw(entity.getTexture(),bodyDef.position.x,bodyDef.position.y);
    };

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    /**
     * Disposing internal resources
     */
    public void dispose() {
        this.entity.dispose();
    }
}
