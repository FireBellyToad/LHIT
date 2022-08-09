package com.faust.lhengine.game.instances;

/**
 * Interface for adding game instances in something during runtime (spawning)
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public interface Spawner {

    <T extends GameInstance> void spawnInstance(Class<T> instanceClass, float startX, float startY, String instanceIdentifierEnum);
}
