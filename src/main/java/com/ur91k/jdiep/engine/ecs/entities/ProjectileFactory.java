package com.ur91k.jdiep.engine.ecs.entities;

import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.entities.base.EntityFactory;
import org.joml.Vector2f;

public class ProjectileFactory implements EntityFactory {
    private final World world;
    
    public ProjectileFactory(World world) {
        this.world = world;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    public Entity createBullet(Vector2f position, Vector2f direction, float damage, float speed) {
        // TODO:
        // - Create bullet entity
        // - Add ProjectileComponent with damage/speed
        // - Add MovementComponent
        // - Add CollisionComponent
        // - Add HealthComponent (for bullet-bullet collisions)
        // - Add LifetimeComponent (bullets despawn)
        return null;
    }
    
    public Entity createTrap(Vector2f position, float damage) {
        // TODO: Future implementation
        // - Similar to bullet but with different behavior
        // - Stays stationary after deployment
        // - Different collision properties
        return null;
    }
} 