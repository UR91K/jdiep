package com.ur91k.jdiep.engine.ecs.components.debug.shapes;

import org.joml.Vector4f;

/**
 * Base class for all debug shapes.
 * Provides common properties like color and dynamic state.
 */
public abstract class DebugShape {
    protected Vector4f color;
    protected boolean isDynamic;
    protected float thickness = 1.0f;

    protected DebugShape(Vector4f color) {
        this.color = new Vector4f(color);
        this.isDynamic = false;
    }

    public Vector4f getColor() {
        return color;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    /**
     * Updates the shape's dynamic properties if any.
     * Should be called each frame for dynamic shapes.
     */
    public abstract void update();
} 