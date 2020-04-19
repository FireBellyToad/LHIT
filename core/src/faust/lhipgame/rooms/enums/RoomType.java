package faust.lhipgame.rooms.enums;

/**
 * Room type enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum RoomType {

    EMPTY_SPACE(""),
    CASUAL("test.tmx"),
    CEMETER_TOP("cemeteryTop.tmx"),
    CEMETER_CENTER("cemeteryCenter.tmx"),
    CEMETER_RIGHT("cemeteryRight.tmx"),
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
