package faust.lhipgame.instances;

import faust.lhipgame.instances.impl.PlayerInstance;

/**
 * Interface for describing interaction with player instance
 */
public interface Interactable {

    void doPlayerInteraction(PlayerInstance playerInstance);
    
    void endPlayerInteraction(PlayerInstance playerInstance);
}
