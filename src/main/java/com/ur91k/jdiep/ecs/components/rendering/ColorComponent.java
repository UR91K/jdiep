package com.ur91k.jdiep.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import org.joml.Vector4f;

public class ColorComponent implements Component {
    private Vector4f fillColor;
    private Vector4f outlineColor;
    private float outlineWidth;
    private boolean hasOutline;

    public ColorComponent() {
        // Default constructor for Ashley's pooling
        this.fillColor = new Vector4f(1, 1, 1, 1);  // White
        this.outlineColor = new Vector4f(0, 0, 0, 1);  // Black
        this.outlineWidth = 0.0f;
        this.hasOutline = false;
    }

    public void init(Vector4f fillColor) {
        this.fillColor.set(fillColor);
        this.hasOutline = false;
        this.outlineWidth = 0.0f;
    }

    public void setOutline(Vector4f outlineColor, float width) {
        this.outlineColor.set(outlineColor);
        this.outlineWidth = width;
        this.hasOutline = true;
    }

    // Primary color getter used by render systems
    public Vector4f getColor() {
        return getFillColor();
    }

    public Vector4f getFillColor() {
        return new Vector4f(fillColor);
    }

    public Vector4f getOutlineColor() {
        return new Vector4f(outlineColor);
    }

    public float getOutlineWidth() {
        return outlineWidth;
    }

    public boolean hasOutline() {
        return hasOutline;
    }
}
