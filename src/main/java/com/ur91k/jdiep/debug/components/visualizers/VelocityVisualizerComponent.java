package com.ur91k.jdiep.debug.components.visualizers;

import com.ur91k.jdiep.ecs.core.Component;
import org.joml.Vector4f;

public class VelocityVisualizerComponent extends Component {
    private final Vector4f color;
    private final float scale;
    private boolean visible = true;

    public VelocityVisualizerComponent(Vector4f color, float scale) {
        this.color = new Vector4f(color);
        this.scale = scale;
    }

    public Vector4f getColor() {
        return new Vector4f(color);
    }

    public float getScale() {
        return scale;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
} 