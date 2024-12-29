package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

/**
 * Component that handles tank control input, abstracting the source (local player, AI, network)
 */
public class TankControllerComponent implements Component {
    private Vector2f moveDirection;
    private float aimAngle;
    private boolean isShooting;
    
    public TankControllerComponent() {
        this.moveDirection = new Vector2f();
        this.aimAngle = 0.0f;
        this.isShooting = false;
    }
    
    public Vector2f getMoveDirection() {
        return new Vector2f(moveDirection);
    }
    
    public void setMoveDirection(Vector2f direction) {
        this.moveDirection.set(direction);
        // Normalize if not zero
        if (direction.lengthSquared() > 0) {
            this.moveDirection.normalize();
        }
    }
    
    public float getAimAngle() {
        return aimAngle;
    }
    
    public void setAimAngle(float angle) {
        this.aimAngle = angle;
    }
    
    public boolean isShooting() {
        return isShooting;
    }
    
    public void setShooting(boolean shooting) {
        this.isShooting = shooting;
    }
} 