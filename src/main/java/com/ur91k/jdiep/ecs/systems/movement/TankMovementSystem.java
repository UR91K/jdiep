package com.ur91k.jdiep.ecs.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.gameplay.TankControllerComponent;
import com.ur91k.jdiep.ecs.components.physics.VelocityComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;

public class TankMovementSystem extends IteratingSystem {
    private final ComponentMapper<TankControllerComponent> controllerMapper;
    private final ComponentMapper<VelocityComponent> velocityMapper;
    private final ComponentMapper<TransformComponent> transformMapper;

    public TankMovementSystem() {
        super(Family.all(
            TankControllerComponent.class,
            VelocityComponent.class,
            TransformComponent.class
        ).get());
        
        this.controllerMapper = ComponentMapper.getFor(TankControllerComponent.class);
        this.velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TankControllerComponent controller = controllerMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        // Apply movement
        Vector2f moveDir = controller.getMoveDirection();
        moveDir.mul(velocity.getAcceleration() * deltaTime);
        Vector2f currentVel = velocity.getVelocity();
        currentVel.add(moveDir);
        
        // Apply aim rotation
        transform.setRotation(controller.getAimAngle());
    }
} 