package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.gameplay.DroneComponent;
import com.ur91k.jdiep.ecs.components.gameplay.HealthComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DroneFactory {
    private final Engine engine;
    
    public enum DroneType {
        GUARD,      // Neutral guard drone
        BASE,       // Team base drone
        OVERLORD,   // Player controlled triangle drone
        NECRO       // Converted square drone
    }
    
    public DroneFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createDrone(DroneType type, Vector2f position) {
        Entity drone = engine.createEntity();
        
        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        drone.add(transform);
        
        // Add movement component
        MovementComponent movement = engine.createComponent(MovementComponent.class);
        movement.init(150.0f);  // Default drone speed
        drone.add(movement);
        
        // Add health component
        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.init(100.0f);  // Default drone health
        drone.add(health);
        
        // Add collision component
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.init(20.0f);  // Default drone radius
        drone.add(collision);
        
        // Add shape component
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        switch (type) {
            case OVERLORD:
                shape.init(new Vector2f[] {  // Triangle shape
                    new Vector2f(-10, -10),
                    new Vector2f(10, -10),
                    new Vector2f(0, 10)
                });
                break;
            default:
                shape.init(20.0f);  // Circle shape
        }
        drone.add(shape);
        
        // Add color component
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(new Vector4f(0.7f, 0.7f, 0.7f, 1.0f));  // Default gray color
        drone.add(color);
        
        // Add drone behavior component
        DroneComponent droneComp = engine.createComponent(DroneComponent.class);
        droneComp.init(type);
        drone.add(droneComp);
        
        engine.addEntity(drone);
        return drone;
    }
    
    public Entity convertFoodToDrone(Entity foodEntity) {
        if (foodEntity == null) {
            return null;
        }
        
        // Check if the entity has a transform component (basic validation)
        TransformComponent foodTransform = foodEntity.getComponent(TransformComponent.class);
        if (foodTransform == null) {
            return null;
        }
        
        // Create new drone at food's position
        Entity drone = createDrone(DroneType.NECRO, foodTransform.getPosition());
        
        // Remove the food entity
        engine.removeEntity(foodEntity);
        
        return drone;
    }
} 