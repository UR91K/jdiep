package com.ur91k.jdiep.ecs.components.rendering;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

public class ShapeComponent implements Component {
    public enum ShapeType {
        CIRCLE,
        RECTANGLE,
        POLYGON
    }

    private ShapeType type;
    private float width;   // For rectangles, or diameter for circles
    private float height;  // For rectangles only
    private Vector2f[] vertices;  // For polygons

    public ShapeComponent() {
        // Default constructor for Ashley's pooling
        this.type = ShapeType.CIRCLE;
        this.width = 1.0f;
        this.height = 1.0f;
        this.vertices = null;
    }

    // Initialize as circle
    public void init(float radius) {
        this.type = ShapeType.CIRCLE;
        this.width = radius * 2;  // Store diameter
        this.height = radius * 2;
        this.vertices = null;
    }

    // Initialize as rectangle with centered origin
    public void init(float width, float height) {
        this.type = ShapeType.RECTANGLE;
        this.width = width;
        this.height = height;
        this.vertices = null;
    }

    // Initialize as rectangle with custom origin point (x, y relative to dimensions)
    public void init(float width, float height, Vector2f origin) {
        this.type = ShapeType.RECTANGLE;
        this.width = width;
        this.height = height;
        // Store vertices for custom origin rectangles
        this.vertices = new Vector2f[] {
            new Vector2f(origin.x, origin.y),
            new Vector2f(origin.x + width, origin.y),
            new Vector2f(origin.x + width, origin.y + height),
            new Vector2f(origin.x, origin.y + height)
        };
    }

    // Initialize as polygon
    public void init(Vector2f[] vertices) {
        this.type = ShapeType.POLYGON;
        this.vertices = vertices.clone();  // Clone to prevent external modification
        // Calculate bounding box for width/height
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        for (Vector2f v : vertices) {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
        }
        this.width = maxX - minX;
        this.height = maxY - minY;
    }

    public ShapeType getType() {
        return type;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRadius() {
        if (type != ShapeType.CIRCLE) {
            throw new IllegalStateException("Cannot get radius of non-circle shape");
        }
        return width / 2;
    }

    public Vector2f[] getVertices() {
        if (type != ShapeType.POLYGON || vertices == null) {
            throw new IllegalStateException("Cannot get vertices of non-polygon shape");
        }
        return vertices.clone();  // Return copy to prevent external modification
    }

    public Vector2f getDimensions() {
        return new Vector2f(width, height);
    }
}
