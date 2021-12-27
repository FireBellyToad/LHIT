package faust.lhitgame.game.ai;

import com.badlogic.gdx.ai.pfa.Connection;

/**
 **
 * Path model that connects two Path Nodes and precalculates its cost
 */
/**
 * Entity instanced in game world class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class Path implements Connection<PathNode> {

    PathNode fromPathNode;
    PathNode toPathNode;
    float cost;

    public Path(PathNode fromPathNode, PathNode toPathNode){
        this.fromPathNode = fromPathNode;
        this.toPathNode = toPathNode;
        cost = fromPathNode.dst(toPathNode);
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public PathNode getFromNode() {
        return fromPathNode;
    }

    @Override
    public PathNode getToNode() {
        return toPathNode;
    }
}
