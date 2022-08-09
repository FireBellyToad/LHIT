package com.faust.lhengine.game.gameentities.enums;

import com.faust.lhengine.game.instances.AnimatedInstance;
import com.faust.lhengine.game.instances.impl.*;

public enum EnemyEnum {
    STRIX(StrixInstance.class),
    BOUNDED(BoundedInstance.class),
    HIVE(HiveInstance.class),
    SPITTER(SpitterInstance.class),
    MEAT(MeatInstance.class),
    PORTAL(PortalInstance.class),
    WILLOWISP(WillowispInstance.class),
    ESCAPE_PORTAL(EscapePortalInstance.class),
    DIACONUS(DiaconusInstance.class),
    UNDEFINED(null);

    private final Class<? extends AnimatedInstance> instanceClass;

    <T extends AnimatedInstance>  EnemyEnum(Class<T> instanceClass) {
        this.instanceClass = instanceClass;
    }

    public Class<? extends AnimatedInstance> getInstanceClass() {
        return instanceClass;
    }

}
