package faust.lhipgame.splash.enums;

public enum SplashScreenEnum {
    STRIX_SPLASH("splash/strix_splash.png");

    private String splashPath;

    public String getSplashPath() {
        return splashPath;
    }

    SplashScreenEnum(String splashPath) {
        this.splashPath = splashPath;
    }
}
