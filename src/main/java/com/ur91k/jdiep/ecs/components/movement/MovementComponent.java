package com.ur91k.jdiep.ecs.components.movement;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

public class MovementComponent implements Component {
    private Vector2f inputDirection;  // Current raw input direction
    private boolean movementEnabled;  // Whether movement input is enabled
    private float moveSpeed;          // Base movement speed

    public MovementComponent() {
        // Default constructor for Ashley's pooling
        this.inputDirection = new Vector2f();
        this.movementEnabled = true;
        this.moveSpeed = 200.0f;  // Default speed
    }

    public void init(float moveSpeed) {
        this.moveSpeed = moveSpeed;
        this.inputDirection.set(0, 0);
        this.movementEnabled = true;
    }

    public Vector2f getInputDirection() {
        return new Vector2f(inputDirection);  // Return copy to prevent external modification
    }

    public void setInputDirection(Vector2f inputDirection) {
        this.inputDirection.set(inputDirection);
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public void setMovementEnabled(boolean enabled) {
        this.movementEnabled = enabled;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
} 