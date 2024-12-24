package com.ur91k.jdiep.engine.ecs.components;

import org.joml.Vector2f;

import com.ur91k.jdiep.engine.ecs.components.base.Component;

public class MovementComponent extends Component {
    private Vector2f velocity;
    private float moveSpeed;
    private float friction;

    public MovementComponent() {
        this(200.0f, 0.9f);  // Default values
    }

    public MovementComponent(float moveSpeed, float friction) {
        this.velocity = new Vector2f();
        this.moveSpeed = moveSpeed;
        this.friction = friction;
    }

    public Vector2f getVelocity() { 
        return new Vector2f(velocity); // Return a copy to prevent external modification
    }
    
    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);  // Copy the values
    }
    
    public float getMoveSpeed() { return moveSpeed; }
    public float getFriction() { return friction; }
    
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public void setFriction(float friction) { this.friction = friction; }
    
    // Helper method to apply friction
    public void applyFriction(float deltaTime) {
        if (velocity.length() > 0) {
            velocity.mul(1.0f - (friction * deltaTime));
            
            // Stop completely if velocity is very small
            if (velocity.length() < 0.01f) {
                velocity.zero();
            }
        }
    }
} 