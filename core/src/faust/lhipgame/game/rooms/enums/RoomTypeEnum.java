package faust.lhipgame.game.rooms.enums;

/**
 * Room type enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum RoomTypeEnum {

    EMPTY_SPACE(""),
    CASUAL("casual%d.tmx"),
    CEMETERY_TOP("cemeteryTop.tmx"),
    CEMETERY_CENTER("cemeteryCenter.tmx"),
    CEMETERY_RIGHT("cemeteryRight.tmx"),
    CHURCH_ENTRANCE("churchEntrance.tmx"),
    CHURCH_RIGHT("churchRight.tmx"),
    CHURCH_BOTTOM("churchBottom.tmx"),
    CHURCH_LEFT("churchLeft.tmx"),
    TREE_STUMP("treeStump.tmx"),
    BOAT("boat.tmx"),
    START_POINT("start.tmx"),
    CRUCIFIED("crucified.tmx");

    private final String mapFileName;

    RoomTypeEnum(String mapFileName) {
        this.mapFileName = mapFileName;
    }

    /**
     *
     * @param roomTypeEnum
     * @return true if roomType has echo
     */
    public static boolean hasEchoes(RoomTypeEnum roomTypeEnum) {
        return CRUCIFIED.equals(roomTypeEnum) ||
                TREE_STUMP.equals(roomTypeEnum);
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public static RoomTypeEnum getFromString(String name) {
        for (RoomTypeEnum e : RoomTypeEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
