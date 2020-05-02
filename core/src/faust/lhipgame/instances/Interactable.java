package faust.lhipgame.instances;

import faust.lhipgame.instances.impl.PlayerInstance;

public interface Interactable {

    void doPlayerInteraction(PlayerInstance playerInstance);
    
    void endPlayerInteraction(PlayerInstance playerInstance);
}
