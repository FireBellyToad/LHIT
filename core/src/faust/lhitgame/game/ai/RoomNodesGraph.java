package faust.lhitgame.game.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import faust.lhitgame.game.world.interfaces.RayCaster;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Path node Graph class, used in a single Room
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomNodesGraph implements IndexedGraph<PathNode> {

    //Container lists
    private final Array<PathNode> nodeArray = new Array<>();
    private final Array<Path> pathArray = new Array<>();

    // Mapping all Paths starting from a PathNode
    Map<PathNode, Array<Path>> graphMap = new HashMap<>();

    public void addPathNode(PathNode pathNode) {
        pathNode.index = nodeArray.size;
        nodeArray.add(pathNode);
    }

    /**
     * Calculate graph
     */
    public void calculateAll(RayCaster rayCaster) {
        //Raycast search callback. If at least one StaticBody is in the line of the ray,
        //the path should not be saved for pathfinding.
        //Why AtomicBoolean? So that it can be changed on callback thread
        AtomicBoolean isConnected = new AtomicBoolean(true);
        RayCastCallback checkIfPathIsFree = (fixture, point, normal, fraction) -> {
            if (Objects.nonNull(fixture.getBody()) &&
                    BodyDef.BodyType.StaticBody.equals(fixture.getBody().getType())) {
                isConnected.set(false);
            }
            return isConnected.get() ? 1 : 0;
        };

        //For each node, check if thers is a connection with another one
        for (PathNode fromNode : nodeArray) {
            for (int index = 0; index < nodeArray.size; index++) {
                PathNode toNode = nodeArray.get(index);

                //Skip same node
                if (fromNode.index == toNode.index) {
                    continue;
                }
                //init check flag
                isConnected.set(true);

                //If connection is free, connect two nodes
                rayCaster.rayCast(checkIfPathIsFree, fromNode, toNode);
                if (isConnected.get()) {
                    connectNodes(fromNode, toNode);
                }
            }
        }
    }

    /**
     * Add to Graph a new Path
     *
     * @param fromPathNode
     * @param toPathNode
     */
    public void connectNodes(PathNode fromPathNode, PathNode toPathNode) {
        final Path newPath = new Path(fromPathNode, toPathNode);
        //Create if not present
        if (!graphMap.containsKey(fromPathNode)) {
            graphMap.put(fromPathNode, new Array<>());
        }
        graphMap.get(fromPathNode).add(newPath);
        pathArray.add(newPath);
    }

    @Override
    public int getIndex(PathNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return nodeArray.size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Array<Connection<PathNode>> getConnections(PathNode fromNode) {
        final Array value = graphMap.getOrDefault(fromNode, null);
        //remember that Path implements Connection<PathNode>
        return Objects.isNull(value) ? new Array<>() : value;
    }

    public Array<PathNode> getNodeArray() {
        return nodeArray;
    }
}
