package com.ur91k.jdiep.engine.ecs.systems;

import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.core.Input;
import com.ur91k.jdiep.engine.core.Time;
import com.ur91k.jdiep.engine.ecs.components.MovementComponent;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.ecs.entities.Entity;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MovementSystem extends System {
    private static final Logger logger = Logger.getLogger(MovementSystem.class);
    private final Input input;
    private Entity player;

    public MovementSystem(Input input) {
        this.input = input;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    @Override
    public void update() {
        if (player == null) return;

        MovementComponent movement = player.getComponent(MovementComponent.class);
        TransformComponent transform = player.getComponent(TransformComponent.class);
        if (movement == null || transform == null) return;

        // Get input direction
        Vector2f moveDir = new Vector2f(0, 0);
        if (input.isKeyPressed(GLFW_KEY_W)) moveDir.y += 1;
        if (input.isKeyPressed(GLFW_KEY_S)) moveDir.y -= 1;
        if (input.isKeyPressed(GLFW_KEY_A)) moveDir.x -= 1;
        if (input.isKeyPressed(GLFW_KEY_D)) moveDir.x += 1;

        // Normalize if moving diagonally
        if (moveDir.lengthSquared() > 0) {
            moveDir.normalize();
        }

        // Update movement
        updateMovement(movement, transform, moveDir, (float)Time.getDeltaTime());
    }

    private void updateMovement(MovementComponent movement, TransformComponent transform, Vector2f moveDir, float deltaTime) {
        if (movement == null || transform == null) {
            logger.warn("Attempted to update null component");
            return;
        }

        // Apply acceleration based on input direction
        Vector2f acceleration = new Vector2f(moveDir).mul(movement.getMoveSpeed());
        movement.getVelocity().add(new Vector2f(acceleration).mul(deltaTime));

        // Apply friction
        Vector2f velocity = movement.getVelocity();
        if (velocity.lengthSquared() > 0) {
            float speed = velocity.length();
            float drop = speed * movement.getFriction() * deltaTime;
            float newSpeed = Math.max(0, speed - drop);
            if (newSpeed != speed) {
                velocity.mul(newSpeed / speed);
            }
        }

        // Update transform position using translate
        transform.translate(new Vector2f(velocity).mul(deltaTime));

        // Debug logging for significant velocity changes
        if (velocity.length() > 100) {
            logger.debug("High velocity detected: {}", velocity.length());
        }
    }
} 