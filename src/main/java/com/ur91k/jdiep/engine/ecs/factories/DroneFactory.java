package com.ur91k.jdiep.engine.ecs.factories;

import org.joml.Vector2f;

import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.World;

public class DroneFactory implements EntityFactory {
    private final World world;
    
    public enum DroneType {
        GUARD,      // Neutral guard drone
        BASE,       // Team base drone
        OVERLORD,   // Player controlled triangle drone
        NECRO       // Converted square drone
    }
    
    public DroneFactory(World world) {
        this.world = world;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    public Entity createDrone(DroneType type, Vector2f position) {
        // TODO:
        // - Create drone entity
        // - Add DroneComponent with AI behavior config
        // - Add HealthComponent
        // - Add MovementComponent
        // - Add CollisionComponent
        // - Add TeamComponent for team-based drones
        // - Add OwnerComponent for player-controlled drones
        return null;
    }
    
    public Entity convertFoodToDrone(Entity foodEntity) {
        // TODO:
        // - Verify food entity is valid for conversion
        // - Create drone maintaining food's position
        // - Transfer relevant properties
        // - Delete original food entity
        return null;
    }
} 