package faust.lhipgame.rooms.enums;

/**
 * Room type enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum RoomType {

    EMPTY_SPACE(""),
    CASUAL("test.tmx"),
    CEMETER_TOP("castrumEntrance.tmx"),
    CEMETER_CENTER("castrumEntrance.tmx"),
    CEMETER_RIGHT("castrumEntrance.tmx"),
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
