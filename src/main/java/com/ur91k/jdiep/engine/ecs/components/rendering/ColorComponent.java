package com.ur91k.jdiep.engine.ecs.components.rendering;

import org.joml.Vector4f;

import com.ur91k.jdiep.engine.ecs.core.Component;

public class ColorComponent extends Component {
    private Vector4f fillColor = new Vector4f(1.0f);
    private Vector4f outlineColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    private float outlineThickness = 1.0f;
    
    public ColorComponent(Vector4f fillColor) {
        this.fillColor.set(fillColor);
    }
    
    public void setOutline(Vector4f color, float thickness) {
        this.outlineColor.set(color);
        this.outlineThickness = thickness;
    }

    public Vector4f getFillColor() {
        return new Vector4f(fillColor);
    }

    public Vector4f getOutlineColor() {
        return new Vector4f(outlineColor);
    }

    public float getOutlineThickness() {
        return outlineThickness;
    }
}
