package faust.lhipgame.gameentities;

import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.gameentities.GameEntity;

public class POIEntity extends GameEntity {

    public static final String FOUND_ITEM_MESSAGE_KEY = "poi.success.examine";

    private String messageKey;

    public POIEntity(String messageKey) {
        super(new Texture("sprites/poi_sheet.png"));
        //TODO parametrizzare
        this.messageKey = messageKey;
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
