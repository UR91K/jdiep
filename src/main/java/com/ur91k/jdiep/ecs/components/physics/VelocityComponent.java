package com.ur91k.jdiep.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

public class VelocityComponent implements Component {
    private Vector2f velocity;
    private float acceleration;
    private float friction;

    public VelocityComponent() {
        this.velocity = new Vector2f();
        this.acceleration = 2000.0f;
        this.friction = 0.95f;
    }

    public VelocityComponent(float acceleration, float friction) {
        this();
        this.acceleration = acceleration;
        this.friction = friction;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
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