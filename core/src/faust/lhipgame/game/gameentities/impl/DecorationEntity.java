package faust.lhipgame.gameentities.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.gameentities.enums.DecorationsEnum;

/**
 * Entity class for Decorations
 */
public class DecorationEntity extends SpriteEntity {

    private DecorationsEnum type;

    public DecorationEntity(DecorationsEnum decorationType, AssetManager assetManager) {
        super(assetManager.get("sprites/decorations_sheet.png"), decorationType.ordinal());

        this.type = decorationType;
    }

    @Override
    protected int getTextureColumns() {
        return 2;
    }

    @Override
    protected int getTextureRows() {
        return DecorationsEnum.values().length;
    }

    public DecorationsEnum getType() {
        return type;
    }

    /**
     *
     * @return true if decoration is passable
     */
    public boolean isPassable() {
        switch (type){
            case GRASS:
            case PLANT:{
                return true;
            }
            default:{
                return false;
            }
        }
    }
}
