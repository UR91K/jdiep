package com.ur91k.jdiep.ecs.components.transform;

import org.joml.Vector2f;

import com.ur91k.jdiep.engine.ecs.core.Component;
import com.ur91k.jdiep.engine.ecs.core.Entity;

public class ParentComponent extends Component {
    private Entity parent;
    private Vector2f localOffset;  // Offset from parent's position
    private float localRotation;   // Rotation relative to parent

    public ParentComponent(Entity parent) {
        this.parent = parent;
        this.localOffset = new Vector2f(0, 0);
        this.localRotation = 0.0f;
    }

    public ParentComponent(Entity parent, Vector2f localOffset, float localRotation) {
        this.parent = parent;
        this.localOffset = new Vector2f(localOffset);
        this.localRotation = localRotation;
    }

    public Entity getParent() {
        return parent;
    }

    public Vector2f getLocalOffset() {
        return new Vector2f(localOffset);
    }

    public void setLocalOffset(Vector2f offset) {
        this.localOffset.set(offset);
    }

    public float getLocalRotation() {
        return localRotation;
    }

    public void setLocalRotation(float rotation) {
        this.localRotation = rotation;
    }
} 