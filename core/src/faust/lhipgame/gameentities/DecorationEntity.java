package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.gameentities.enums.DecorationsEnum;

public class DecorationEntity extends SpriteEntity {

    private DecorationsEnum type;

    public DecorationEntity(DecorationsEnum decorationType) {
        super(new Texture("sprites/decorations_sheet.png"), decorationType.ordinal());

        this.type = decorationType;
    }

    @Override
    protected int getTextureColumns() {
        return 2;
    }

    @Override
    protected int getTextureRows() {
        return 6;
    }

    public DecorationsEnum getType() {
        return type;
    }

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
