package faust.lhipgame.gameentities.impl;

import com.badlogic.gdx.graphics.Texture;
import faust.lhipgame.gameentities.SpriteEntity;
import faust.lhipgame.gameentities.enums.ItemEnum;
import faust.lhipgame.gameentities.enums.POIEnum;

/**
 * POI Entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class POIEntity extends SpriteEntity {

    public static final String FOUND_ITEM_MESSAGE_KEY_SUFFIX = ".success";
    public static final String EXAMINING_ITEM_MESSAGE_KEY_SUFFIX = ".examine";

    private String messageKey;
    private ItemEnum itemGiven;
    private String splashKey;

    public POIEntity(POIEnum type) {
        super(new Texture("sprites/poi_sheet.png"), type.ordinal());
        this.messageKey = type.getTextKey();
        this.itemGiven = type.getItemGiven();
        this.splashKey = type.getSplashKey();
    }

    public String getMessageKey() {
        return messageKey;
    }

    public ItemEnum getItemGiven() {
        return itemGiven;
    }

    public String getSplashKey() {
        return splashKey;
    }

    @Override
    protected int getTextureColumns() {
        return 1;
    }

    @Override
    protected int getTextureRows() {
        return 2;
    }

}
