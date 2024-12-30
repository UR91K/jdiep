package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

/**
 * Component that handles drone physics-based movement and behavior
 */
public class DroneControllerComponent implements Component {
    private Vector2f targetPosition;  // Position the drone is trying to reach
    private float targetAngle;       // Desired rotation angle (radians)
    private float maxForce;          // Maximum force that can be applied (Newtons)
    private float maxTorque;         // Maximum torque that can be applied (N⋅m)
    private float orbitRadius;       // Distance to maintain from target when orbiting
    private float orbitSpeed;        // Angular velocity for orbit movement (radians/sec)
    
    public DroneControllerComponent() {
        this.targetPosition = new Vector2f();
        this.targetAngle = 0.0f;
        this.maxForce = 400.0f;      // Default max force
        this.maxTorque = 200.0f;     // Default max torque
        this.orbitRadius = 50.0f;    // Default orbit radius
        this.orbitSpeed = 2.0f;      // Default orbit speed (radians/sec)
    }
    
    public Vector2f getTargetPosition() {
        return new Vector2f(targetPosition);
    }
    
    public void setTargetPosition(Vector2f position) {
        this.targetPosition.set(position);
    }
    
    public float getTargetAngle() {
        return targetAngle;
    }
    
    public void setTargetAngle(float angle) {
        this.targetAngle = angle;
    }
    
    public float getMaxForce() {
        return maxForce;
    }
    
    public void setMaxForce(float maxForce) {
        if (maxForce < 0) {
            throw new IllegalArgumentException("Max force cannot be negative");
        }
        this.maxForce = maxForce;
    }
    
    public float getMaxTorque() {
        return maxTorque;
    }
    
    public void setMaxTorque(float maxTorque) {
        if (maxTorque < 0) {
            throw new IllegalArgumentException("Max torque cannot be negative");
        }
        this.maxTorque = maxTorque;
    }
    
    public float getOrbitRadius() {
        return orbitRadius;
    }
    
    public void setOrbitRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Orbit radius cannot be negative");
        }
        this.orbitRadius = radius;
    }
    
    public float getOrbitSpeed() {
        return orbitSpeed;
    }
    
    public void setOrbitSpeed(float speed) {
        this.orbitSpeed = speed;
    }
} 