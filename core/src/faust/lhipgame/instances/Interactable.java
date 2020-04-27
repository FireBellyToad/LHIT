package faust.lhipgame.instances;

public interface Interactable {

    void doPlayerInteraction(PlayerInstance playerInstance);
    
    void endPlayerInteraction(PlayerInstance playerInstance);
}
