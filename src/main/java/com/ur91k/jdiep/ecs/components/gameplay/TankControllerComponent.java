package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

/**
 * Component that handles tank control input state, for use with physics-based movement
 */
public class TankControllerComponent implements Component {
    private Vector2f moveForce;      // Current movement force direction
    private float targetAngle;       // Target angle for tank body (radians)
    private boolean isShooting;      // Shooting state
    private float maxForce;          // Maximum force that can be applied (Newtons)
    private float maxTorque;         // Maximum torque that can be applied (N⋅m)
    
    public TankControllerComponent() {
        this.moveForce = new Vector2f();
        this.targetAngle = 0.0f;
        this.isShooting = false;
        this.maxForce = 1000.0f;     // Default max force
        this.maxTorque = 500.0f;     // Default max torque
    }
    
    public Vector2f getMoveForce() {
        return new Vector2f(moveForce);
    }
    
    public void setMoveForce(Vector2f force) {
        this.moveForce.set(force);
        // Normalize if not zero
        if (force.lengthSquared() > 0) {
            this.moveForce.normalize();
        }
    }
    
    public float getTargetAngle() {
        return targetAngle;
    }
    
    public void setTargetAngle(float angle) {
        this.targetAngle = angle;
    }
    
    public boolean isShooting() {
        return isShooting;
    }
    
    public void setShooting(boolean shooting) {
        this.isShooting = shooting;
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
} 