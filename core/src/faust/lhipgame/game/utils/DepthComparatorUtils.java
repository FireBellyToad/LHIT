package faust.lhipgame.game.utils;

import faust.lhipgame.game.gameentities.enums.DecorationsEnum;
import faust.lhipgame.game.gameentities.enums.POIEnum;
import faust.lhipgame.game.gameentities.interfaces.Hurtable;
import faust.lhipgame.game.instances.GameInstance;
import faust.lhipgame.game.instances.impl.DecorationInstance;
import faust.lhipgame.game.instances.impl.POIInstance;
import faust.lhipgame.game.instances.impl.StrixInstance;

public class DepthComparatorUtils {

    // Compares two GameInstances by y depth
    public static int compareEntities(GameInstance o1, GameInstance o2) {

        //Special conditions to place object always on higher depth, usually
        //for avoiding that objects laying on the ground cover taller ones
        if ((o2 instanceof Hurtable && ((Hurtable) o2).isDead()) ||
                (o1 instanceof StrixInstance && ((StrixInstance) o1).isAttachedToPlayer()) ||
                (o2 instanceof DecorationInstance && DecorationsEnum.ALLY_CORPSE_1.equals(((DecorationInstance) o2).getType())) ||
                (o2 instanceof DecorationInstance && DecorationsEnum.ALLY_CORPSE_2.equals(((DecorationInstance) o2).getType())) ||
                (o2 instanceof DecorationInstance && ((DecorationInstance) o2).getInteracted()) ||
                (o2 instanceof POIInstance && POIEnum.SKELETON.equals(((POIInstance) o2).getType()))) {
            return 1;
        }

        if ((o1 instanceof Hurtable && ((Hurtable) o1).isDead()) ||
                (o2 instanceof StrixInstance && ((StrixInstance) o2).isAttachedToPlayer())) {
            return -1;
        }

        //or else just sort by Y axis
        if ((o1.getBody().getPosition().y < o2.getBody().getPosition().y)) {
            return 1;
        } else if (o1.getBody().getPosition().y > o2.getBody().getPosition().y) {
            return -1;
        }

        return 0;
    }

}
