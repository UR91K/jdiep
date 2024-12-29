package com.ur91k.jdiep.ecs.components.transform;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import org.joml.Vector2f;

public class ParentComponent implements Component {
    private Entity parent;
    private Vector2f localOffset;  // Offset from parent's position
    private float localRotation;   // Rotation relative to parent

    public ParentComponent() {
        // Default constructor for Ashley's pooling
        this.localOffset = new Vector2f(0, 0);
        this.localRotation = 0.0f;
        this.parent = null;
    }

    public void init(Entity parent, Vector2f offset, float rotation) {
        this.parent = parent;
        this.localOffset.set(offset);
        this.localRotation = rotation;
    }

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Vector2f getLocalOffset() {
        return new Vector2f(localOffset);  // Return copy to prevent external modification
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

    /**
     * Helper method to calculate world position based on parent's transform
     */
    public Vector2f getWorldPosition(TransformComponent parentTransform) {
        if (parentTransform == null) return new Vector2f(localOffset);
        
        // Rotate offset by parent's rotation
        Vector2f rotatedOffset = new Vector2f(localOffset);
        float cos = (float) Math.cos(parentTransform.getRotation());
        float sin = (float) Math.sin(parentTransform.getRotation());
        float x = rotatedOffset.x * cos - rotatedOffset.y * sin;
        float y = rotatedOffset.x * sin + rotatedOffset.y * cos;
        
        // Add parent's position
        return new Vector2f(x, y).add(parentTransform.getPosition());
    }

    /**
     * Helper method to calculate world rotation based on parent's transform
     */
    public float getWorldRotation(TransformComponent parentTransform) {
        if (parentTransform == null) return localRotation;
        return localRotation + parentTransform.getRotation();
    }
} 