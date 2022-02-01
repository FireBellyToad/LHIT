package com.faust.lhitgame.utils;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.faust.lhitgame.game.instances.impl.DecorationInstance;
import com.faust.lhitgame.game.instances.impl.PlayerInstance;
import com.faust.lhitgame.game.rooms.areas.EmergedArea;
import com.faust.lhitgame.game.rooms.areas.WallArea;

import java.util.Objects;

/**
 * All raycasting logic should be put here to keep it clean
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RayCastUtils {

    /**
     *
     * @param fixture
     * @param target Player
     * @return true if Fixture is a Wall, a non Passable Decoration or the Player
     */
    public static boolean isPlayerOrWall(Fixture fixture, PlayerInstance target) {
        return fixture.getBody().getUserData() instanceof WallArea || // is Wall
                (fixture.getBody().getUserData() instanceof DecorationInstance &&
                        !((DecorationInstance) fixture.getBody().getUserData()).isPassable()) || // is a non passable Decoration
                fixture.getBody().equals(target.getBody()); // is Player
    }

    /**
     *
     * @param fixture
     * @return true if Fixture is anything non passable
     */
    public static boolean isNotPassable(Fixture fixture) {
        return Objects.nonNull(fixture.getBody()) && // has Body
                !(fixture.getBody().getUserData() instanceof EmergedArea) && // is not an EmergedArea
                !(fixture.getBody().getUserData() instanceof DecorationInstance && ((DecorationInstance) fixture.getBody().getUserData()).isPassable()); // is not a non passable Decoration
    }
}
