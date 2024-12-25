package com.ur91k.jdiep.engine.ecs.systems.movement;

import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.systems.core.GameSystem;
import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.core.time.Time;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;

import org.joml.Vector2f;

public class MovementStateSystem extends GameSystem {
    private static final Logger logger = Logger.getLogger(MovementStateSystem.class);
    
    public MovementStateSystem(World world, boolean isClient) {
        super(world, isClient);
    }
    
    @Override
    public void update() {
        double deltaTime = Time.getDeltaTime();
        
        for (Entity entity : world.getEntitiesWith(MovementComponent.class, TransformComponent.class)) {
            updateEntityMovement(entity, (float)deltaTime); // note to self: make sure this conversion doesnt break random shit
        }
    }
    
    private void updateEntityMovement(Entity entity, float deltaTime) {
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        
        if (movement == null || transform == null) {
            logger.warn("Attempted to update null component");
            return;
        }
        
        // Apply acceleration to velocity
        movement.applyAcceleration(deltaTime);
        
        // Apply friction to slow down
        movement.applyFriction(deltaTime);
        
        // Get current state
        Vector2f currentPos = transform.getPosition();
        Vector2f velocity = movement.getVelocity();
        
        // Update position based on velocity
        Vector2f newPosition = new Vector2f(
            currentPos.x + velocity.x * deltaTime,
            currentPos.y + velocity.y * deltaTime
        );
        
        transform.setPosition(newPosition);
        
        // Debug logging for significant velocity changes
        if (velocity.length() > 500) {
            logger.debug("High velocity detected for entity {}: {}", entity.getId(), velocity.length());
        }
    }
} 