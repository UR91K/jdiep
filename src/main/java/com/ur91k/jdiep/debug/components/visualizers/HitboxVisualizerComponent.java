package com.ur91k.jdiep.debug.components.visualizers;

import com.ur91k.jdiep.ecs.core.Component;
import org.joml.Vector4f;

public class HitboxVisualizerComponent extends Component {
    private final Vector4f color;
    private final float radius;
    private boolean visible = true;

    public HitboxVisualizerComponent(Vector4f color, float radius) {
        this.color = new Vector4f(color);
        this.radius = radius;
    }

    public Vector4f getColor() {
        return new Vector4f(color);
    }

    public float getRadius() {
        return radius;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
} 