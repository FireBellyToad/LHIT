package faust.lhipgame.game.music.enums;

public enum TuneEnum {

    TITLE("title.ogg"),
    AMBIENCE("ambience.ogg"),
    GAMEOVER("gameover.ogg");
//    DANGER("ambience"),
//    FIGHT("ambience"),
//    CHURCH("ambience");

    private String fileName;

    TuneEnum(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return "music/" +fileName;
    }
}
