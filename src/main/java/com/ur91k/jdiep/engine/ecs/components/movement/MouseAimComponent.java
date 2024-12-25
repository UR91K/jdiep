package com.ur91k.jdiep.engine.ecs.components.movement;

import com.ur91k.jdiep.engine.ecs.core.Component;
import com.ur91k.jdiep.engine.ecs.core.Entity;

public class MouseAimComponent extends Component {
    private Entity target;  // The entity whose rotation we'll modify (e.g., turret)

    public MouseAimComponent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
} 