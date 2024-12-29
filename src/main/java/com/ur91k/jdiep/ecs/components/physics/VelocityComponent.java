package com.ur91k.jdiep.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

public class VelocityComponent implements Component {
    private Vector2f velocity;
    private float maxSpeed;
    private float acceleration;
    private float friction;

    public VelocityComponent() {
        this.velocity = new Vector2f();
        this.maxSpeed = 500.0f;
        this.acceleration = 2000.0f;
        this.friction = 0.95f;
    }

    public VelocityComponent(float maxSpeed, float acceleration, float friction) {
        this();
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.friction = friction;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        // Clamp to max speed
        if (this.velocity.length() > maxSpeed) {
            this.velocity.normalize().mul(maxSpeed);
        }
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }
} 