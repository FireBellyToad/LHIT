package faust.lhipgame.echoes.enums;

public enum EchoesActorType {
    DISCIPULUS("echo.discipulus.json","sprites/discipulus_sheet.png"),
    VICTIM("echo.victim.json","sprites/victim_sheet.png"),
    CADAVER("echo.cadaver.json","sprites/cadaver_sheet.png");

    private String filename;
    private String spriteFilename;

    EchoesActorType(String filename, String spriteFilename) {
        this.filename = filename;
        this.spriteFilename = spriteFilename;
    }

    public String getFilename() {
        return filename;
    }


    public static EchoesActorType getFromString(String name) {
        for (EchoesActorType e : EchoesActorType.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public String getSpriteFilename() {
        return spriteFilename;
    }
}
