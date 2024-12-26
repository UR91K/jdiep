package com.ur91k.jdiep.ecs.components.movement;

import org.joml.Vector2f;

import com.ur91k.jdiep.ecs.core.Component;

public class MovementComponent extends Component {
    private Vector2f velocity;
    private Vector2f acceleration;
    private Vector2f inputDirection;  // Current raw input direction
    private float moveSpeed;
    private float friction;

    public MovementComponent() {
        this(200.0f, 0.9f);  // Default values
    }

    public MovementComponent(float moveSpeed, float friction) {
        this.velocity = new Vector2f();
        this.acceleration = new Vector2f();
        this.inputDirection = new Vector2f();
        this.moveSpeed = moveSpeed;
        this.friction = friction;
    }

    public Vector2f getVelocity() { 
        return new Vector2f(velocity); // Return a copy to prevent external modification
    }
    
    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);  // Copy the values
    }
    
    public Vector2f getAcceleration() {
        return new Vector2f(acceleration);
    }
    
    public void setAcceleration(Vector2f acceleration) {
        this.acceleration.set(acceleration);
    }

    public Vector2f getInputDirection() {
        return new Vector2f(inputDirection);
    }

    public void setInputDirection(Vector2f inputDirection) {
        this.inputDirection.set(inputDirection);
    }
    
    public float getMoveSpeed() { return moveSpeed; }
    public float getFriction() { return friction; }
    
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public void setFriction(float friction) { this.friction = friction; }
    
    // Apply acceleration to velocity
    public void applyAcceleration(float deltaTime) {
        velocity.add(new Vector2f(acceleration).mul(deltaTime));
    }
    
    // Apply friction to current velocity
    public void applyFriction(float deltaTime) {
        if (velocity.lengthSquared() > 0) {
            float speed = velocity.length();
            float drop = speed * friction * deltaTime;
            float newSpeed = Math.max(0, speed - drop);
            
            if (newSpeed != speed) {
                velocity.mul(newSpeed / speed);
            }
            
            // Stop completely if velocity is very small
            if (velocity.lengthSquared() < 0.01f) {
                velocity.zero();
            }
        }
    }
} 