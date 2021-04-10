package faust.lhipgame.game.music;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import faust.lhipgame.game.music.enums.TuneEnum;

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

    public MusicManager(AssetManager assetManager) {

        for(TuneEnum tune : TuneEnum.values()){
            musicMap.put(tune,assetManager.get("music/"+tune.getFileName()));
        }
    }

    /**
     * Play looping tune
     * @param tune to play
     */
    public void playMusic(TuneEnum tune){
        playMusic(tune,1f,true);
    }

    /**
     * Play looping tune
     * @param tune to play
     * @param volume
     */
    public void playMusic(TuneEnum tune, float volume){
        playMusic(tune,volume,true);
    }

    /**
     * Play a tune
     * @param tune to play
     * @param volume
     * @param loop true if must loop
     */
    public void playMusic(TuneEnum tune, float volume, boolean loop) {
        Objects.requireNonNull(tune);

        final Music tuneToPlay = musicMap.get(tune);
        Objects.requireNonNull(tuneToPlay);

        tuneToPlay.play();
        tuneToPlay.setLooping(loop);
        tuneToPlay.setVolume(volume);
    }

    /**
     * Stop all music
     */
    public void stopMusic(){
        musicMap.forEach((tune,music) -> {
            music.stop();
        });
    }
}
