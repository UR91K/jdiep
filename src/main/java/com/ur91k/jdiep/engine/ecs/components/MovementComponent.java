package com.ur91k.jdiep.engine.ecs.components;

import org.joml.Vector2f;

import com.ur91k.jdiep.engine.ecs.components.base.Component;

public class MovementComponent extends Component {
    private Vector2f velocity;
    private float moveSpeed;
    private float friction;

    public MovementComponent(float moveSpeed, float friction) {
        this.velocity = new Vector2f();
        this.moveSpeed = moveSpeed;
        this.friction = friction;
    }

    public Vector2f getVelocity() { return velocity; }
    public float getMoveSpeed() { return moveSpeed; }
    public float getFriction() { return friction; }
    
    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public void setFriction(float friction) { this.friction = friction; }
} 