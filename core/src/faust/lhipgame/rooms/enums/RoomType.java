package faust.lhipgame.rooms.enums;

/**
 * Room type enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum RoomType {

    EMPTY_SPACE(""),
    CASUAL("test.tmx"),
    CEMETERY_TOP("cemeteryTop.tmx"),
    CEMETERY_CENTER("cemeteryCenter.tmx"),
    CEMETERY_RIGHT("cemeteryRight.tmx"),
    CHURCH_ENTRANCE("churchEntrance.tmx"),
    TREE_STUMP("treeStump.tmx");

    private String mapFileName;

    RoomType(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public static RoomType getFromString(String name) {
        for (RoomType e : RoomType.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
