package com.ur91k.jdiep.engine.ecs.components.debug.shapes;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * Debug shape for rendering rectangles.
 * Supports both static and dynamic rectangles through suppliers.
 */
public class DebugRect extends DebugShape {
    private Vector2f position;
    private float width;
    private float height;
    private Supplier<Vector2f> positionSupplier;
    private Supplier<Float> widthSupplier;
    private Supplier<Float> heightSupplier;

    public DebugRect(Vector2f position, float width, float height, Vector4f color) {
        super(color);
        this.position = new Vector2f(position);
        this.width = width;
        this.height = height;
    }

    public DebugRect(Supplier<Vector2f> positionSupplier, Supplier<Float> widthSupplier, 
                     Supplier<Float> heightSupplier, Vector4f color) {
        super(color);
        this.positionSupplier = positionSupplier;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.isDynamic = true;
        
        // Initialize values
        update();
    }

    @Override
    public void update() {
        if (isDynamic) {
            if (position == null) position = new Vector2f();
            
            Vector2f newPosition = positionSupplier.get();
            float newWidth = widthSupplier.get();
            float newHeight = heightSupplier.get();
            
            position.set(newPosition);
            width = newWidth;
            height = newHeight;
        }
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
} 