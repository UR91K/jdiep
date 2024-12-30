package com.ur91k.jdiep.ecs.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.gameplay.DroneComponent;
import com.ur91k.jdiep.ecs.components.gameplay.DroneControllerComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;

public class PhysicsDroneControlSystem extends IteratingSystem {
    private final ComponentMapper<DroneComponent> droneMapper;
    private final ComponentMapper<DroneControllerComponent> controllerMapper;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    
    private float orbitTime = 0;  // Track time for orbital movement
    
    public PhysicsDroneControlSystem() {
        super(Family.all(
            DroneComponent.class,
            DroneControllerComponent.class,
            CollisionComponent.class,
            TransformComponent.class
        ).get());
        
        this.droneMapper = ComponentMapper.getFor(DroneComponent.class);
        this.controllerMapper = ComponentMapper.getFor(DroneControllerComponent.class);
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DroneComponent drone = droneMapper.get(entity);
        DroneControllerComponent controller = controllerMapper.get(entity);
        CollisionComponent collision = collisionMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        if (collision.getBody() == null || drone.getOwner() == null) {
            return;  // No physics body or owner yet
        }
        
        // Get owner's position as the center point for orbiting
        TransformComponent ownerTransform = drone.getOwner().getComponent(TransformComponent.class);
        if (ownerTransform == null) return;
        
        Vector2f ownerPos = ownerTransform.getPosition();
        
        // Calculate orbital position
        orbitTime += deltaTime * controller.getOrbitSpeed();
        float orbitX = ownerPos.x + (float)Math.cos(orbitTime) * controller.getOrbitRadius();
        float orbitY = ownerPos.y + (float)Math.sin(orbitTime) * controller.getOrbitRadius();
        controller.setTargetPosition(new Vector2f(orbitX, orbitY));
        
        // Get current position and calculate direction to target
        Vector2f currentPos = transform.getPosition();
        Vector2f targetPos = controller.getTargetPosition();
        Vector2f direction = new Vector2f(targetPos).sub(currentPos);
        float distance = direction.length();
        
        // Apply forces if not at target position
        if (distance > 1.0f) {  // Small threshold to prevent jitter
            direction.normalize();
            
            // Calculate force based on distance (stronger when further away)
            float forceMagnitude = Math.min(controller.getMaxForce(), distance * 10);
            Vec2 force = new Vec2(direction.x * forceMagnitude, direction.y * forceMagnitude);
            
            // Apply force through physics
            org.jbox2d.dynamics.Body body = collision.getBody();
            body.applyForce(force, body.getWorldCenter());
            
            // Calculate desired angle (facing movement direction)
            float targetAngle = (float)Math.atan2(direction.y, direction.x);
            controller.setTargetAngle(targetAngle);
            
            // Apply torque for rotation
            float currentAngle = body.getAngle();
            float angleDiff = normalizeAngle(targetAngle - currentAngle);
            float torque = angleDiff * controller.getMaxTorque();
            
            // Clamp torque to max value
            torque = Math.max(-controller.getMaxTorque(), Math.min(controller.getMaxTorque(), torque));
            body.applyTorque(torque);
        }
    }
    
    // Normalize angle to [-π, π]
    private float normalizeAngle(float angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
} 