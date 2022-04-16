package com.faust.lhitgame.game.instances;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.faust.lhitgame.game.ai.PathNode;
import com.faust.lhitgame.game.ai.RoomNodesGraph;
import com.faust.lhitgame.game.gameentities.TexturedEntity;
import com.faust.lhitgame.utils.PathfinderUtils;

import java.util.Objects;

/**
 * Smart distancer instance class, used on things that should keep distance from a target smartly:
 * - if too close then do A* in nodegraph targeting the farthest node
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class DistancerInstance extends AnimatedInstance {

    //Current path node to follow
    protected PathNode targetPathNode;
    //Target instance
    protected final GameInstance target;

    protected boolean recalculatePath = true;

    //Queue of nodes to follow
    protected final Queue<PathNode> pathQueue = new Queue<>();
    private PathNode currentPos;
    private PathNode newGoal;

    public DistancerInstance(TexturedEntity entity, GameInstance target) {
        super(entity);
        this.target = target;
        this.targetPathNode = new PathNode(target.getBody().getPosition());
    }

    /**
     * Set the goal PathNode, calculate a path, and start moving.
     */
    public void calculateNewGoal(RoomNodesGraph roomNodesGraph) {

        //Do not calculate if has no nodesGraph or if target is in line of sight
        if (!recalculatePath || Objects.isNull(roomNodesGraph))
            return;

        final Array<PathNode> nodeArray = roomNodesGraph.getNodeArray();

        //get nearest to This
        nodeArray.sort((n1, n2) -> Float.compare(body.getPosition().dst(n1), body.getPosition().dst(n2)));
        currentPos = nodeArray.get(0);

        //get farthest to Target
        nodeArray.sort((n1, n2) -> Float.compare(target.getBody().getPosition().dst(n2), target.getBody().getPosition().dst(n1)));
        newGoal = nodeArray.get(0);

        final GraphPath<PathNode> graphPath = PathfinderUtils.generatePath(currentPos, newGoal, roomNodesGraph);
        for (PathNode pathNode : graphPath) {
            pathQueue.addLast(pathNode);

            //Limit queue size
            if(pathQueue.size >= 3){
                break;
            }
        }
        recalculatePath = pathQueue.isEmpty();
        targetPathNode = pathQueue.isEmpty() ? currentPos : pathQueue.removeFirst();
    }

    /**
     * Check if should go to target or another point
     *
     * @return
     */
    protected Vector2 getMovementDestination() {

        //If node is reached
        if (body.getPosition().dst(targetPathNode) <= 5) {
            if (pathQueue.isEmpty()) {
                //If path is finished, recalculate
                recalculatePath = true;
            } else {
                //Else go to next node in path
                targetPathNode = pathQueue.removeFirst();
            }
        }

        return targetPathNode;
    }

    /**
     * FIXME REMOVE
     */
    public void drawDebug(OrthographicCamera cameraTemp) {

        ShapeRenderer shapeRenderer = new ShapeRenderer();
        Color back = new Color(0xffffffff);

        PathNode previous = null;
        for (PathNode p : pathQueue) {

            if (Objects.isNull(previous)) {
                previous = p;
                continue;
            }
            shapeRenderer.setColor(back);
            shapeRenderer.setProjectionMatrix(cameraTemp.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.line(p, previous);
            shapeRenderer.end();
            previous = p;
        }

        if (Objects.nonNull(currentPos)) {

            shapeRenderer.setColor(back);
            shapeRenderer.setProjectionMatrix(cameraTemp.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(currentPos.x, currentPos.y, 5);
            shapeRenderer.end();
        }
        if (Objects.nonNull(newGoal)) {

            shapeRenderer.setColor(back);
            shapeRenderer.setProjectionMatrix(cameraTemp.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(newGoal.x, newGoal.y, 5);
            shapeRenderer.end();
        }
    }
}
