package com.ur91k.jdiep.engine.ecs.systems.movement;

import com.ur91k.jdiep.engine.ecs.World;
import com.ur91k.jdiep.engine.ecs.systems.base.GameSystem;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.components.MovementComponent;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.core.Time;
import org.joml.Vector2f;

public class MovementStateSystem extends GameSystem {
    
    public MovementStateSystem(World world, boolean isClient) {
        super(world, isClient);
    }
    
    @Override
    public void update() {
        double deltaTime = Time.getDeltaTime();
        
        for (Entity entity : world.getEntitiesWith(MovementComponent.class, TransformComponent.class)) {
            updateEntityMovement(entity, deltaTime);
        }
    }
    
    private void updateEntityMovement(Entity entity, double deltaTime) {
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        
        // Apply friction
        movement.applyFriction((float)deltaTime);
        
        // Get current state
        Vector2f currentPos = transform.getPosition();
        Vector2f velocity = movement.getVelocity();
        
        // Update position based on velocity
        Vector2f newPosition = new Vector2f(
            currentPos.x + velocity.x * (float)deltaTime,
            currentPos.y + velocity.y * (float)deltaTime
        );
        
        transform.setPosition(newPosition);
    }
} 