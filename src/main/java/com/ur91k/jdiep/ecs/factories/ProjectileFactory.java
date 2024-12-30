package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.gameplay.ProjectileComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.tinylog.Logger;

public class ProjectileFactory {
    private static final float BULLET_SPEED_MULTIPLIER = 20.0f; // Adjust for desired bullet speed
    private final Engine engine;
    
    public ProjectileFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createBullet(Vector2f position, Vector2f direction, float speed, float damage, Entity owner) {
        Entity bullet = engine.createEntity();
        
        // Add transform
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        // Set rotation to match direction
        transform.setRotation((float) Math.atan2(direction.y, direction.x));
        bullet.add(transform);
        
        // Add projectile component
        ProjectileComponent projectile = engine.createComponent(ProjectileComponent.class);
        projectile.init(damage, owner);
        bullet.add(projectile);
        
        // Add collision (small circle for bullets)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float radius = 5.0f;  // Small bullet size
        collision.init(bullet, radius, CollisionFilters.CATEGORY_BULLET, CollisionFilters.MASK_BULLET);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.1f);  // Very light
        collision.setFriction(0.0f);  // No friction
        collision.setRestitution(0.0f);  // No bounce
        collision.setLinearDamping(0.0f);  // No drag
        collision.setAngularDamping(0.0f);  // No rotation drag
        collision.setBullet(true);  // Enable continuous collision detection
        bullet.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(radius);  // Circle shape
        bullet.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS + 1);  // Render above tanks
        bullet.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.BULLET_FILL_COLOR);
        color.setOutline(RenderingConstants.BULLET_OUTLINE_COLOR, 1.0f);
        bullet.add(color);
        
        // Add all components before applying initial velocity
        engine.addEntity(bullet);
        
        // Apply initial velocity through physics
        CollisionComponent collisionComp = bullet.getComponent(CollisionComponent.class);
        if (collisionComp.getBody() != null) {
            float mass = collisionComp.getBody().getMass();
            Vec2 impulse = new Vec2(
                direction.x * speed * BULLET_SPEED_MULTIPLIER * mass,
                direction.y * speed * BULLET_SPEED_MULTIPLIER * mass
            );
            collisionComp.getBody().setLinearVelocity(new Vec2(impulse.x, impulse.y));
            // Disable rotation
            collisionComp.getBody().setFixedRotation(true);
        }
        
        Logger.debug("Created bullet at position: {} with direction: {}", position, direction);
        return bullet;
    }
} 