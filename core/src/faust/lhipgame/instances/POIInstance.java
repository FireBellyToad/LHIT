package faust.lhipgame.instances;

import faust.lhipgame.gameentities.POIEntity;

public class POIInstance extends GameInstance {

    public POIInstance() {
        super(new POIEntity());
    }

    public void examine() {
        //TODO
        System.out.println("POI EXAMINED");
    }

    ;
}
