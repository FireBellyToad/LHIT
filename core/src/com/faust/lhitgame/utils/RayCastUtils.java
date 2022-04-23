package com.faust.lhitgame.utils;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.faust.lhitgame.game.instances.AnimatedInstance;
import com.faust.lhitgame.game.instances.GameInstance;
import com.faust.lhitgame.game.instances.impl.DecorationInstance;
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
     * @param fixture to check
     * @param target GameInstance of the target
     * @return true if Fixture is a Wall, a non Passable Decoration or the GameInstance
     */
    public static boolean isTargetOrWall(Fixture fixture, GameInstance target) {
        return fixture.getBody().getUserData() instanceof WallArea || // is Wall
                (fixture.getBody().getUserData() instanceof DecorationInstance &&
                        !((DecorationInstance) fixture.getBody().getUserData()).isPassable()) || // is a non passable Decoration
                fixture.getBody().equals(target.getBody()); // is target
    }

    /**
     *
     * @param fixture
     * @return true if Fixture is anything non passable
     */
    public static boolean isNotPassable(Fixture fixture) {
        return Objects.nonNull(fixture.getBody()) && // has Body
                !(fixture.getBody().getUserData() instanceof EmergedArea) && // is not an EmergedArea
                !(ClassReflection.isAssignableFrom(AnimatedInstance.class, fixture.getBody().getUserData().getClass())) && // is not an AnimatedInstance
                !(fixture.getBody().getUserData() instanceof DecorationInstance && ((DecorationInstance) fixture.getBody().getUserData()).isPassable()); // is not a non passable Decoration
    }
}
