package com.ur91k.jdiep.debug.components.visualizers;

import com.ur91k.jdiep.ecs.core.Component;
import org.joml.Vector4f;

public class InputVisualizerComponent extends Component {
    private final Vector4f color;
    private final float length;
    private boolean visible = true;

    public InputVisualizerComponent(Vector4f color, float length) {
        this.color = new Vector4f(color);
        this.length = length;
    }

    public Vector4f getColor() {
        return new Vector4f(color);
    }

    public float getLength() {
        return length;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
} 