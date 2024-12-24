package com.ur91k.jdiep.engine.ecs.components.debug.shapes;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * Debug shape for rendering circles.
 * Supports both static and dynamic circles through suppliers.
 */
public class DebugCircle extends DebugShape {
    private Vector2f center;
    private float radius;
    private Supplier<Vector2f> centerSupplier;
    private Supplier<Float> radiusSupplier;

    public DebugCircle(Vector2f center, float radius, Vector4f color) {
        super(color);
        this.center = new Vector2f(center);
        this.radius = radius;
    }

    public DebugCircle(Supplier<Vector2f> centerSupplier, Supplier<Float> radiusSupplier, Vector4f color) {
        super(color);
        this.centerSupplier = centerSupplier;
        this.radiusSupplier = radiusSupplier;
        this.isDynamic = true;
        
        // Initialize values
        update();
    }

    @Override
    public void update() {
        if (isDynamic) {
            if (center == null) center = new Vector2f();
            
            Vector2f newCenter = centerSupplier.get();
            float newRadius = radiusSupplier.get();
            
            center.set(newCenter);
            radius = newRadius;
        }
    }

    public Vector2f getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }
} 