package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.gameplay.HealthComponent;
import com.ur91k.jdiep.ecs.components.gameplay.LifetimeComponent;
import com.ur91k.jdiep.ecs.components.gameplay.ProjectileComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ProjectileFactory {
    private final Engine engine;
    private static final float DEFAULT_BULLET_LIFETIME = 2.0f;  // Seconds
    private static final float DEFAULT_BULLET_RADIUS = 5.0f;
    
    public ProjectileFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createBullet(Vector2f position, Vector2f direction, float damage, float speed) {
        Entity bullet = engine.createEntity();
        
        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        transform.setRotation((float) Math.atan2(direction.y, direction.x));
        bullet.add(transform);
        
        // Add movement component with direction and speed
        MovementComponent movement = engine.createComponent(MovementComponent.class);
        movement.init(speed);
        movement.setInputDirection(direction);
        bullet.add(movement);
        
        // Add projectile component with damage
        ProjectileComponent projectile = engine.createComponent(ProjectileComponent.class);
        projectile.init(damage);
        bullet.add(projectile);
        
        // Add collision component
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.init(DEFAULT_BULLET_RADIUS);
        bullet.add(collision);
        
        // Add health component for bullet-bullet collisions
        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.init(1.0f);  // One-hit destroy
        bullet.add(health);
        
        // Add lifetime component for auto-despawn
        LifetimeComponent lifetime = engine.createComponent(LifetimeComponent.class);
        lifetime.init(DEFAULT_BULLET_LIFETIME);
        bullet.add(lifetime);
        
        // Add shape component
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(DEFAULT_BULLET_RADIUS);
        bullet.add(shape);
        
        // Add color component
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(new Vector4f(0.8f, 0.2f, 0.2f, 1.0f));  // Red bullets
        bullet.add(color);
        
        engine.addEntity(bullet);
        return bullet;
    }
    
    public Entity createTrap(Vector2f position, float damage) {
        Entity trap = engine.createEntity();
        
        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        trap.add(transform);
        
        // Add projectile component with damage
        ProjectileComponent projectile = engine.createComponent(ProjectileComponent.class);
        projectile.init(damage);
        trap.add(projectile);
        
        // Add collision component
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.init(DEFAULT_BULLET_RADIUS * 1.5f);  // Slightly larger than bullets
        trap.add(collision);
        
        // Add health component
        HealthComponent health = engine.createComponent(HealthComponent.class);
        health.init(10.0f);  // More durable than bullets
        trap.add(health);
        
        // Add shape component (square for traps)
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        float size = DEFAULT_BULLET_RADIUS * 2;
        shape.init(size, size);
        trap.add(shape);
        
        // Add color component
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(new Vector4f(0.8f, 0.4f, 0.0f, 1.0f));  // Orange traps
        trap.add(color);
        
        engine.addEntity(trap);
        return trap;
    }
} 