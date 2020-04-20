package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import faust.lhipgame.gameentities.GameEntity;
import faust.lhipgame.gameentities.enums.DecorationsEnum;
import faust.lhipgame.gameentities.enums.POIEnum;

import java.util.Arrays;

public class POIEntity extends SpriteEntity {

    public static final String FOUND_ITEM_MESSAGE_KEY = "poi.success.examine";

    private String messageKey;

    public POIEntity(POIEnum type) {
        super(new Texture("sprites/poi_sheet.png"), type.ordinal());
        this.messageKey = type.getTextKey();
    }

    public String getMessageKey(){
        return messageKey;
    }

    @Override
    protected int getTextureColumns() {
        return 1;
    }

    @Override
    protected int getTextureRows() {
        return 1;
    }

}
