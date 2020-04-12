package faust.lhipgame.rooms.enums;

/**
 * Room type enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum RoomType {
    CASUAL("test.tmx"),
    CASTRUM_ENTRANCE("castrumEntrance.tmx"),
    CHURCH_ENTRANCE("churchEntrance.tmx"),
    TREE_STUMP("treeStump.tmx");

    private String mapFileName;

    RoomType(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    public String getMapFileName() {
        return mapFileName;
    }
}
