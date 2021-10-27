package faust.lhitgame.game.music;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import faust.lhitgame.game.music.enums.TuneEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Music manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MusicManager {

    private final Map<TuneEnum, Music> musicMap = new HashMap<>();
    private boolean disableMusic = false;

    /**
     * Load and put in map a single tune
     *
     * @param tune
     * @param assetManager
     */
    public void loadSingleTune(TuneEnum tune, AssetManager assetManager) {
        Objects.requireNonNull(tune);
        Objects.requireNonNull(assetManager);

        assetManager.load(tune.getFileName(), Music.class);
        assetManager.finishLoading();
        musicMap.put(tune, assetManager.get(tune.getFileName()));
    }

    /**
     * Load all music from files. MusicManager.initTuneMap MUST BE CALLED AFTER
     *
     * @param assetManager
     */
    public void loadMusicFromFiles(AssetManager assetManager) {
        Objects.requireNonNull(assetManager);
        for (TuneEnum tune : TuneEnum.values()) {
            assetManager.load(tune.getFileName(), Music.class);
        }
    }

    /**
     * Should be called AFTER MusicManager.loadMusicFromFiles
     *
     * @param assetManager
     */
    public void initTuneMap(AssetManager assetManager) {
        Objects.requireNonNull(assetManager);

        for (TuneEnum tune : TuneEnum.values()) {
            musicMap.put(tune, assetManager.get(tune.getFileName()));
        }
    }

    /**
     * Play looping tune
     *
     * @param tune to play
     */
    public void playMusic(TuneEnum tune) {
        playMusic(tune, 1f, true);
    }

    /**
     * Play looping tune
     *
     * @param tune   to play
     * @param volume
     */
    public void playMusic(TuneEnum tune, float volume) {
        playMusic(tune, volume, true);
    }

    /**
     * Play tune
     *
     * @param tune to play
     */
    public void playMusic(TuneEnum tune, boolean loop) {
        playMusic(tune, 1f, loop);
    }

    /**
     * Play a tune
     *
     * @param tune   to play
     * @param volume
     * @param loop   true if must loop
     */
    public void playMusic(TuneEnum tune, float volume, boolean loop) {
        Objects.requireNonNull(tune);
        // If tune is enabled, play it
        if (!disableMusic) {
            final Music tuneToPlay = musicMap.get(tune);
            Objects.requireNonNull(tuneToPlay);

            //If tune is not already playing, stop previous and play it
            if (!tuneToPlay.isPlaying()) {
                stopMusic();
                tuneToPlay.play();
                tuneToPlay.setLooping(loop);
                tuneToPlay.setVolume(volume);
            }
        }
    }

    /**
     * Toggle music
     *
     * @return the current status of the music
     */
    public boolean toggleMusic() {
        if (disableMusic) {
            disableMusic = false;
        } else {
            disableMusic = true;
            stopMusic();
        }

        return disableMusic;
    }

    /**
     * Stop all music
     */
    public void stopMusic() {
        musicMap.forEach((tune, music) -> music.stop());
    }

    /**
     *
     * @param tune
     * @return true if is playing
     */
    public boolean isPlaying(TuneEnum tune) {
        final Music tuneToCheck = musicMap.get(tune);
        Objects.requireNonNull(tuneToCheck);
        return tuneToCheck.isPlaying();
    }

    /**
     *
     * @return true if any music is playing
     */
    public boolean isPlaying() {
        for(TuneEnum tune : musicMap.keySet()){
            final Music tuneToCheck = musicMap.get(tune);
            if(tuneToCheck.isPlaying()){
                return  true;
            }
        }
        return  false;
    }
}
