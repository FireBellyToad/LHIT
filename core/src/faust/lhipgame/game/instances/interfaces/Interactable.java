package faust.lhipgame.game.instances.interfaces;

import faust.lhipgame.game.instances.impl.PlayerInstance;

/**
 * Interface for describing interaction with player instance
 */
public interface Interactable {

    void doPlayerInteraction(PlayerInstance playerInstance);
    
    void endPlayerInteraction(PlayerInstance playerInstance);
}
