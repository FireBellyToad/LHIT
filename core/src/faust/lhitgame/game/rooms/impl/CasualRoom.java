package faust.lhitgame.game.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import faust.lhitgame.game.instances.impl.PlayerInstance;
import faust.lhitgame.game.music.MusicManager;
import faust.lhitgame.game.music.enums.TuneEnum;
import faust.lhitgame.game.rooms.AbstractRoom;
import faust.lhitgame.game.rooms.enums.RoomFlagEnum;
import faust.lhitgame.game.rooms.enums.RoomTypeEnum;
import faust.lhitgame.game.splash.SplashManager;
import faust.lhitgame.game.textbox.manager.TextBoxManager;
import faust.lhitgame.game.world.manager.WorldManager;
import faust.lhitgame.saves.RoomSaveEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Casual Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CasualRoom extends AbstractRoom {

    public static final List<Integer> GOLDCROSS_MAPS = new ArrayList<Integer>() {{
        this.add(1);
        this.add(5);
        this.add(6);
    }};
    public static final List<Integer> BUSH_MAPS = new ArrayList<Integer>() {{
        this.add(3);
        this.add(8);
    }};

    public static final int CASUAL_TOTAL = 9;
    private int casualNumber;

    public CasualRoom(WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager, RoomSaveEntry roomSaveEntry, Map<RoomFlagEnum,Boolean> roomFlags, MusicManager musicManager) {
        super(RoomTypeEnum.CASUAL, worldManager, textManager, splashManager, player, camera, assetManager, roomSaveEntry, roomFlags, musicManager);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {

        // If has a predefined casual number (like from a savefile or because it was already visited) use that one
        // Or else generate a new number.
        if (Objects.nonNull(roomSaveEntry)) {
            casualNumber = roomSaveEntry.casualNumber;

            // FIXME handle multiple POI
            mustClearPOI = roomSaveEntry.savedFlags.get(RoomFlagEnum.ALREADY_EXAMINED_POIS);

        } else {
            if (roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_GOLDCROSS)) {
                //pick only ones with skeleton poi
                casualNumber = GOLDCROSS_MAPS.get(MathUtils.random(0, 2));
            } else if (roomContent.roomFlags.get(RoomFlagEnum.WITHOUT_HERBS)) {
                //pick only ones without herbs in
                while(BUSH_MAPS.contains(casualNumber)){
                    casualNumber = MathUtils.random(1, CasualRoom.CASUAL_TOTAL);
                }
            } else if (roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_HERBS)) {
                //pick only ones with herbs in
                casualNumber = BUSH_MAPS.get(MathUtils.random(0, 1));
            } else {
                casualNumber = MathUtils.random(1, CasualRoom.CASUAL_TOTAL);
            }
        }
        //Enforce number between 1 and CASUAL_TOTAL. Seemingly unnecessary, but...
        casualNumber = MathUtils.clamp(casualNumber, 1, CasualRoom.CASUAL_TOTAL);

        // Casual maps range from casual1.tmx to casual7.tmx, with a %d to be mapped
        roomContent.roomFileName = roomContent.roomFileName.replace("%d", Integer.toString(casualNumber));

        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomContent.roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    @Override
    protected void onRoomEnter(RoomTypeEnum roomType, WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager) {
        // FIXME handle multiple POI
        if (mustClearPOI) {
            this.roomContent.poiList.forEach(poi -> poi.setAlreadyExamined(true));
        }

        if (roomContent.enemyList.size() > 0) {
            //Loop title music
            musicManager.playMusic(TuneEnum.DANGER, 0.75f);
        } else {
            //Loop title music
            musicManager.playMusic(TuneEnum.AMBIENCE, 0.85f);
        }
    }

    @Override
    public void onRoomLeave() {
        // Nothing to do here... yet
    }

    public int getCasualNumber() {
        return casualNumber;
    }
}
