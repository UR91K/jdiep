package com.ur91k.jdiep.engine.ecs;

public class MouseAimComponent extends Component {
    private Entity target;  // The entity whose rotation we'll modify (e.g., turret)

    public MouseAimComponent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
} 