package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.gameplay.HealthComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class FoodFactory {
    private final Engine engine;
    
    public enum FoodType {
        SQUARE(10),      // Basic food
        TRIANGLE(25),    // Medium food
        PENTAGON(130),   // Large food
        ALPHA_PENTAGON(3000); // Boss food
        
        private final float mass;
        FoodType(float mass) { this.mass = mass; }
        public float getMass() { return mass; }
    }
    
    public FoodFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createFood(FoodType type, Vector2f position) {
        Entity food = engine.createEntity();
        
        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        food.add(transform);
        
        // Add health component based on mass
        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.init(type.getMass());
        food.add(health);
        
        // Add collision component
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.init(calculateRadius(type.getMass()));
        food.add(collision);
        
        // Add shape component based on type
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        switch (type) {
            case SQUARE:
                float size = calculateSize(type.getMass());
                shape.init(size, size);
                break;
            case TRIANGLE:
                shape.init(new Vector2f[] {
                    new Vector2f(-15, -15),
                    new Vector2f(15, -15),
                    new Vector2f(0, 15)
                });
                break;
            case PENTAGON:
            case ALPHA_PENTAGON:
                float radius = calculateRadius(type.getMass());
                Vector2f[] vertices = new Vector2f[5];
                for (int i = 0; i < 5; i++) {
                    float angle = (float) (2 * Math.PI * i / 5);
                    vertices[i] = new Vector2f(
                        (float) (radius * Math.cos(angle)),
                        (float) (radius * Math.sin(angle))
                    );
                }
                shape.init(vertices);
                break;
        }
        food.add(shape);
        
        // Add color component
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(getFoodColor(type));
        food.add(color);
        
        engine.addEntity(food);
        return food;
    }
    
    private float calculateRadius(float mass) {
        return (float) Math.sqrt(mass / Math.PI) * 2;
    }
    
    private float calculateSize(float mass) {
        return (float) Math.sqrt(mass) * 2;
    }
    
    private Vector4f getFoodColor(FoodType type) {
        switch (type) {
            case SQUARE:
                return new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);  // Light gray
            case TRIANGLE:
                return new Vector4f(0.8f, 0.8f, 0.2f, 1.0f);  // Yellow
            case PENTAGON:
                return new Vector4f(0.4f, 0.4f, 1.0f, 1.0f);  // Blue
            case ALPHA_PENTAGON:
                return new Vector4f(0.8f, 0.2f, 0.8f, 1.0f);  // Purple
            default:
                return new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);  // White
        }
    }
} 