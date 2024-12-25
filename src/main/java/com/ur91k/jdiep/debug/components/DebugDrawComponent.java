package com.ur91k.jdiep.debug.components;

import com.ur91k.jdiep.debug.shapes.DebugCircle;
import com.ur91k.jdiep.debug.shapes.DebugLine;
import com.ur91k.jdiep.debug.shapes.DebugShape;
import com.ur91k.jdiep.ecs.core.Component;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DebugDrawComponent extends Component {
    private final List<DebugShape> shapes = new ArrayList<>();
    private boolean visible = true;
    private boolean screenSpace = false;

    public List<DebugShape> getShapes() {
        return shapes;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isScreenSpace() {
        return screenSpace;
    }

    public DebugDrawComponent setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public DebugDrawComponent setScreenSpace(boolean screenSpace) {
        this.screenSpace = screenSpace;
        return this;
    }

    public DebugDrawComponent addLine(Vector2f start, Vector2f end, Vector4f color) {
        shapes.add(new DebugLine(start, end, color));
        return this;
    }

    public DebugDrawComponent addDynamicLine(Supplier<Vector2f> startSupplier, Supplier<Vector2f> endSupplier, Vector4f color) {
        shapes.add(new DebugLine(startSupplier, endSupplier, color));
        return this;
    }

    public DebugDrawComponent addCircle(Vector2f center, float radius, Vector4f color) {
        shapes.add(new DebugCircle(center, radius, color));
        return this;
    }

    public DebugDrawComponent addDynamicCircle(Supplier<Vector2f> centerSupplier, Supplier<Float> radiusSupplier, Vector4f color) {
        shapes.add(new DebugCircle(centerSupplier, radiusSupplier, color));
        return this;
    }

    public void update() {
        if (!visible) return;
        shapes.forEach(DebugShape::update);
    }

    public void clear() {
        shapes.clear();
    }
} 