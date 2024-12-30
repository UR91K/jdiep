package com.ur91k.jdiep.ecs.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.gameplay.TankControllerComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TurretComponent;
import com.ur91k.jdiep.ecs.components.physics.RevoluteJointComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.tinylog.Logger;

/**
 * System that manages revolute joint motors for turrets.
 * Applies motor forces to rotate turrets to their target angles while considering physical interactions.
 */
public class TurretMotorSystem extends IteratingSystem {
    private final ComponentMapper<RevoluteJointComponent> jointMapper;
    private final ComponentMapper<TurretComponent> turretMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<TankControllerComponent> controllerMapper;

    private static final float ANGLE_TOLERANCE = 0.01f;  // Radians
    private static final float MIN_MOTOR_SPEED = 0.1f;   // Minimum speed to prevent jitter
    
    public TurretMotorSystem() {
        super(Family.all(
            RevoluteJointComponent.class,
            TurretComponent.class,
            TransformComponent.class
        ).get());
        
        this.jointMapper = ComponentMapper.getFor(RevoluteJointComponent.class);
        this.turretMapper = ComponentMapper.getFor(TurretComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.controllerMapper = ComponentMapper.getFor(TankControllerComponent.class);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RevoluteJointComponent jointComp = jointMapper.get(entity);
        TurretComponent turretComp = turretMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        RevoluteJoint joint = jointComp.getJoint();
        if (joint == null) {
            Logger.warn("Turret has no joint created yet");
            return;
        }
        
        // Get current angles
        float currentAngle = joint.getJointAngle();
        float targetAngle = jointComp.getTargetAngle();
        
        // Calculate shortest rotation path
        float angleDiff = calculateShortestRotation(currentAngle, targetAngle);
        
        // If we're close enough to target, stop the motor
        if (Math.abs(angleDiff) < ANGLE_TOLERANCE) {
            joint.setMotorSpeed(0);
            return;
        }
        
        // Calculate motor speed based on angle difference
        float baseSpeed = jointComp.getMotorSpeed();
        float speedMultiplier = Math.min(Math.abs(angleDiff), 1.0f);  // Slow down as we get closer
        float motorSpeed = Math.max(baseSpeed * speedMultiplier, MIN_MOTOR_SPEED);
        
        // Set motor direction
        motorSpeed *= Math.signum(angleDiff);
        
        // Update joint motor
        joint.setMotorSpeed(motorSpeed);
        
        // Update transform component to match physics state
        transform.setRotation(currentAngle);
    }
    
    /**
     * Calculates the shortest rotation between two angles in radians.
     * Returns the signed difference that represents the shortest path.
     */
    private float calculateShortestRotation(float currentAngle, float targetAngle) {
        float diff = targetAngle - currentAngle;
        
        // Normalize to [-π, π]
        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        
        return diff;
    }
} 