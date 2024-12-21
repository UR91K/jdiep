package com.ur91k.jdiep.engine.ecs;

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
}
