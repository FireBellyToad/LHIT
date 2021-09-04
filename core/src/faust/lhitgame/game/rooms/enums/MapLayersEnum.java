package faust.lhitgame.game.rooms.enums;

/**
 * Map Layer enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum MapLayersEnum {
    TERRAIN_LAYER("terrain"),
    OVERLAY_LAYER("overlay"),
    OBJECT_LAYER("objects");

    private final String layerName;

    MapLayersEnum(String layerName) {
        this.layerName = layerName;
    }

    public String getLayerName() {
        return layerName;
    }
}