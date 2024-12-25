package com.ur91k.jdiep.engine.ecs.entities;

import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.entities.base.EntityFactory;
import org.joml.Vector2f;

public class FoodFactory implements EntityFactory {
    private final World world;
    
    public enum FoodType {
        SQUARE(10),      // Basic food
        TRIANGLE(25),    // Medium food
        PENTAGON(130),   // Large food
        ALPHA_PENTAGON(3000); // Boss food
        
        private final float mass;
        FoodType(float mass) { this.mass = mass; }
        public float getMass() { return mass; }
    }
    
    public FoodFactory(World world) {
        this.world = world;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    public Entity createFood(FoodType type, Vector2f position) {
        // TODO:
        // - Create food entity
        // - Add TransformComponent
        // - Add HealthComponent based on mass
        // - Add CollisionComponent
        // - Add RenderComponent with appropriate shape/color
        // - Add DestructibleComponent (for awarding mass)
        return null;
    }
} 