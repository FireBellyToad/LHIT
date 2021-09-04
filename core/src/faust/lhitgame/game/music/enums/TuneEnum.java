package faust.lhitgame.game.music.enums;

public enum TuneEnum {

    TITLE("title.ogg"),
    AMBIENCE("ambience.ogg"),
    GAMEOVER("gameover.ogg"),
    DANGER("danger.ogg"),
    ATTACK("attack.ogg"),
    FINAL("final.ogg"),
    ENDGAME("gameover.ogg"); // FIXME

    private final String fileName;

    TuneEnum(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return "music/" +fileName;
    }
}
