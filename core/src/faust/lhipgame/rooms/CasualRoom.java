package faust.lhipgame.rooms;

import com.badlogic.gdx.math.MathUtils;
import faust.lhipgame.instances.DecorationInstance;
import faust.lhipgame.instances.POIInstance;
import faust.lhipgame.instances.PlayerInstance;
import faust.lhipgame.text.TextManager;
import faust.lhipgame.world.WorldManager;

import java.util.ArrayList;

/**
 * Casual Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 *
 */
public class CasualRoom extends AbstractRoom {

    public CasualRoom(WorldManager worldManager, TextManager textManager, PlayerInstance player) {
        super(worldManager, textManager, player);
    }

    @Override
    protected void initRoom(WorldManager worldManager, TextManager textManager, PlayerInstance player) {

        this.player = player;

        //Generate Random POIS
        poiList = new ArrayList<POIInstance>();

        for(int i = 0;i < MathUtils.random(2);i++){
            poiList.add(new POIInstance(textManager));
        }

        //Generate Random Decorations
        decorationList = new ArrayList<DecorationInstance>();

        for(int i = 0;i < MathUtils.random(3,10);i++){
            decorationList.add(new DecorationInstance());
        }

        worldManager.insertPOIIntoRoom(poiList);
        worldManager.insertDecorationsIntoRoom(decorationList);
        player.changePOIList(poiList);


    }
}
