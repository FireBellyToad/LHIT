package com.faust.lhitgame.game.music.enums;

public enum TuneEnum {

    TITLE("title.ogg"),
    AMBIENCE("ambience.ogg"),
    GAMEOVER("gameover.ogg"),
    DANGER("danger.ogg"),
    ATTACK("attack.ogg"),
    CHURCH("church.ogg"),
    FINAL("final.ogg"),
    SECRET("secret.ogg"),
    ENDGAME("gameover.ogg");

    private final String fileName;

    TuneEnum(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return "music/" +fileName;
    }
}
