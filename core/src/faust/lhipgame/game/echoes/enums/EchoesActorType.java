package faust.lhipgame.game.echoes.enums;

public enum EchoesActorType {
    DISCIPULUS("echo.discipulus.json","sprites/discipulus_sheet.png"),
    VICTIM("echo.victim.json","sprites/victim_sheet.png"),
    WOMAN("echo.woman.json","sprites/woman_sheet.png",5);

    private final String filename;
    private final String spriteFilename;
    private final int lenght;

    EchoesActorType(String filename, String spriteFilename) {
        this.filename = filename;
        this.spriteFilename = spriteFilename;
        this.lenght = 2;
    }
    EchoesActorType(String filename, String spriteFilename, int lenght) {
        this.filename = filename;
        this.spriteFilename = spriteFilename;
        this.lenght = lenght;
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

    public int getLenght() {
        return lenght;
    }
}
