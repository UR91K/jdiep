package com.ur91k.jdiep.ecs.components.rendering;

import com.ur91k.jdiep.ecs.core.Component;

public class ShapeComponent extends Component {
    public enum ShapeType {
        CIRCLE,
        TRIANGLE,
        RECTANGLE,
        POLYGON
    }
    
    private ShapeType type;
    private float[] vertices;  // For custom polygons
    private float radius;      // For circles
    private int sides;         // For regular polygons
    private float width;       // For rectangles
    private float height;      // For rectangles
    
    // Circle constructor
    public ShapeComponent(float radius) {
        this.type = ShapeType.CIRCLE;
        this.radius = radius;
    }
    
    // Regular polygon constructor
    public ShapeComponent(ShapeType type, float radius, int sides) {
        this.type = type;
        this.radius = radius;
        this.sides = sides;
    }
    
    // Custom polygon constructor
    public ShapeComponent(float[] vertices) {
        this.type = ShapeType.POLYGON;
        this.vertices = vertices;
    }
    
    // Rectangle constructor
    public ShapeComponent(float width, float height) {
        this.type = ShapeType.RECTANGLE;
        this.width = width;
        this.height = height;
    }

    public ShapeType getType() {
        return type;
    }
    
    public void setType(ShapeType type) {
        this.type = type;
    }

    public float getRadius() {
        return radius;
    }

    public int getSides() {
        return sides;
    }

    public float[] getVertices() {
        return vertices;
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
}
