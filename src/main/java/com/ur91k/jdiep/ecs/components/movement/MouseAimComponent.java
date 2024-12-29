package com.ur91k.jdiep.ecs.components.movement;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class MouseAimComponent implements Component {
    private Entity target;  // The entity whose rotation we'll modify (e.g., turret)
    private boolean enabled;  // Whether mouse aiming is currently enabled

    public MouseAimComponent() {
        this.enabled = true;
    }

    public MouseAimComponent(Entity target) {
        this();
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
} 