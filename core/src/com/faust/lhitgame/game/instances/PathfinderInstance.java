package com.faust.lhitgame.game.instances;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.faust.lhitgame.game.ai.PathNode;
import com.faust.lhitgame.game.ai.RoomNodesGraph;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.world.interfaces.RayCaster;
import com.faust.lhitgame.utils.PathfinderUtils;
import com.faust.lhitgame.game.gameentities.GameEntity;
import com.faust.lhitgame.game.instances.impl.DecorationInstance;
import com.faust.lhitgame.game.rooms.areas.WallArea;
import com.faust.lhitgame.utils.Pair;

import java.util.Objects;

/**
 * Smart chaser instance class, used on things that should follow the player smartly:
 * - if can see him, just follow him
 * - if cannot see him, then do A* in nodegraph
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class PathfinderInstance extends AnimatedInstance {

    protected RayCaster rayCaster;

    //Current path node to follow
    protected PathNode targetPathNode;
    //Target player instance
    protected final PlayerInstance target;

    protected boolean isAggressive = false;
    protected boolean recalculatePath = true;

    //Queue of nodes to follow
    protected Queue<PathNode> pathQueue = new Queue<>();
    private PathNode currentPos;
    private PathNode newGoal;

    public PathfinderInstance(GameEntity entity, PlayerInstance target, RayCaster rayCaster) {
        super(entity);
        this.target = target;
        this.rayCaster = rayCaster;
        this.targetPathNode = new PathNode(target.getBody().getPosition());
    }

    /**
     * Set the goal PathNode, calculate a path, and start moving.
     */
    public void calculateNewGoal(RoomNodesGraph roomNodesGraph) {

        if (!recalculatePath || Objects.isNull(roomNodesGraph)|| canSeePlayer())
            return;

        Array<PathNode> nodeArray = roomNodesGraph.getNodeArray();

        //get nearest to This
        nodeArray.sort((n1, n2) -> Float.compare(body.getPosition().dst(n1), body.getPosition().dst(n2)));
        currentPos = nodeArray.get(0);

        //get nearest to Target
        nodeArray.sort((n1, n2) -> Float.compare(target.getBody().getPosition().dst(n1), target.getBody().getPosition().dst(n2)));
        newGoal = nodeArray.get(0);

        GraphPath<PathNode> graphPath = PathfinderUtils.generatePath(currentPos, newGoal, roomNodesGraph);
        graphPath.forEach(pathNode -> {
            pathQueue.addLast(pathNode);

            //limit queue size
            if (pathQueue.size > 2)
                return;
        });
        recalculatePath = pathQueue.isEmpty() ;
        targetPathNode = pathQueue.isEmpty() ? currentPos : pathQueue.removeFirst();
    }

    protected boolean canSeePlayer() {
        //Check if player is in range
        if ((getDistanceFromPlayer() > (getLineOfSight() * 0.75) && !isAggressive) ||
                (getDistanceFromPlayer() > getLineOfSight() && isAggressive)) {
            return false;
        }

        //Do a raycast, save all instances that are caught by the ray
        Array<Pair<Float, Object>> tempInstancesList = new Array<>();
        RayCastCallback getPlayerAndWallsInRay = (fixture, point, normal, fraction) -> {
            //Select only walls, decorations and player (excluding hitboxes)
            if (fixture.getBody().getUserData() instanceof WallArea ||
                    fixture.getBody().getUserData() instanceof DecorationInstance ||
                    fixture.getBody().equals(target.getBody())) {
                tempInstancesList.add(new Pair<>(fraction, fixture.getBody().getUserData()));
            }
            return 1;
        };

        rayCaster.rayCast(getPlayerAndWallsInRay, body.getPosition(), target.getBody().getPosition());
        //Order by distance from this, using fraction
        tempInstancesList.sort((p1, p2) -> Float.compare(p1.getFirst(), p2.getFirst()));
        //Check if first one is player
        return !tempInstancesList.isEmpty() && tempInstancesList.get(0).getSecond() instanceof PlayerInstance;
    }

    /**
     * Can be overridden
     *
     * @return maximum line of sight
     */
    protected float getLineOfSight() {
        return 60f;
    }

    /**
     * Can be overridden
     *
     * @return Distance From Player
     */
    protected float getDistanceFromPlayer() {
        return target.getBody().getPosition().dst(body.getPosition());
    }

    /**
     * Check if should go to player or another point
     *
     * @return
     */
    protected Vector2 getMovementDestination() {

        if (canSeePlayer()) {
            //If can see player, just follow
            recalculatePath = true;
            pathQueue.clear();
            return target.getBody().getPosition();
        } else {

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

    /**
     * Force this instance to use graph
     */
    public void forceRecalculation(){
        recalculatePath = true;
    }
}
