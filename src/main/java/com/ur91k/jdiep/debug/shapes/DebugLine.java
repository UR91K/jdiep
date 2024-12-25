package com.ur91k.jdiep.debug.shapes;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * Debug shape for rendering lines.
 * Supports both static and dynamic lines through suppliers.
 */
public class DebugLine extends DebugShape {
    private Vector2f start;
    private Vector2f end;
    private Supplier<Vector2f> startSupplier;
    private Supplier<Vector2f> endSupplier;

    public DebugLine(Vector2f start, Vector2f end, Vector4f color) {
        super(color);
        this.start = new Vector2f(start);
        this.end = new Vector2f(end);
    }

    public DebugLine(Supplier<Vector2f> startSupplier, Supplier<Vector2f> endSupplier, Vector4f color) {
        super(color);
        this.startSupplier = startSupplier;
        this.endSupplier = endSupplier;
        this.isDynamic = true;
        
        // Initialize positions
        update();
    }

    @Override
    public void update() {
        if (isDynamic) {
            if (start == null) start = new Vector2f();
            if (end == null) end = new Vector2f();
            
            Vector2f newStart = startSupplier.get();
            Vector2f newEnd = endSupplier.get();
            
            start.set(newStart);
            end.set(newEnd);
        }
    }

    public Vector2f getStart() {
        return start;
    }

    public Vector2f getEnd() {
        return end;
    }
} 