package com.ur91k.jdiep.ecs.components.movement;

import com.ur91k.jdiep.ecs.core.Component;
import com.ur91k.jdiep.ecs.core.Entity;

public class MouseAimComponent extends Component {
    private Entity target;  // The entity whose rotation we'll modify (e.g., turret)

    public MouseAimComponent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
} 