package faust.lhipgame.rooms;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.rooms.enums.RoomType;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.Objects;

/**
 * Casual Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CasualRoom extends AbstractRoom {

    public static final int CASUAL_TOTAL = 3;
    private int casualNumber;

    public CasualRoom(WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera,Integer casualNumber) {
        super(RoomType.CASUAL, worldManager, textManager, player, camera,casualNumber);
    }

    @Override
    protected void loadTiledMap(Object[] additionalLoadArguments) {

        // If has a predefined casual number (like from a savefile or because it was already visited) use that one
        // Or else generate a new number.
        if(Objects.nonNull(additionalLoadArguments) && additionalLoadArguments.length >0 && Objects.nonNull(additionalLoadArguments[0])){
            casualNumber = (int) additionalLoadArguments[0];
        } else {
            casualNumber = MathUtils.random(1,CasualRoom.CASUAL_TOTAL);
        }

        // Casual maps range from casual1.tmx to casual6.tmx, with a %d to be mapped
        roomFileName= String.format(roomFileName,casualNumber);

        // Load Tiled map
        tiledMap = new TmxMapLoader().load(roomFileName);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

    }

    @Override
    protected void initRoom(RoomType roomType, WorldManager worldManager, TextManager textManager, PlayerInstance player, OrthographicCamera camera) {


    }

    public int getCasualNumber() {
        return casualNumber;
    }
}
