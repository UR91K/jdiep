package com.ur91k.jdiep.ecs.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.physics.VelocityComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;

public class MovementSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<VelocityComponent> velocityMapper;

    public MovementSystem() {
        super(Family.all(TransformComponent.class, VelocityComponent.class).get());
        
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);
        
        // Update position based on velocity
        Vector2f deltaPosition = new Vector2f(velocity.getVelocity()).mul(deltaTime);
        transform.getPosition().add(deltaPosition);
        
        // Apply friction from velocity component
        velocity.getVelocity().mul(velocity.getFriction());
    }
} 