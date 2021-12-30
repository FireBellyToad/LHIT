package com.faust.lhitgame.game.gameentities.enums;

import com.faust.lhitgame.game.instances.AnimatedInstance;
import com.faust.lhitgame.game.instances.impl.*;

public enum EnemyEnum {
    STRIX(StrixInstance.class),
    BOUNDED(BoundedInstance.class),
    HIVE(HiveInstance.class),
    SPITTER(SpitterInstance.class),
    MEAT(MeatInstance.class),
    PORTAL(PortalInstance.class),
    WILLOWISP(WillowispInstance.class),
    UNDEFINED(null);

    private final Class<? extends AnimatedInstance> instanceClass;

    <T extends AnimatedInstance>  EnemyEnum(Class<T> instanceClass) {
        this.instanceClass = instanceClass;
    }

    public Class<? extends AnimatedInstance> getInstanceClass() {
        return instanceClass;
    }

    public static EnemyEnum getFromString(String name) {
        for (EnemyEnum e : EnemyEnum.values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
