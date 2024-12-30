package com.ur91k.jdiep.ecs.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.physics.VelocityComponent;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MovementInputSystem extends IteratingSystem {
    private final Input input;
    private final ComponentMapper<VelocityComponent> velocityMapper;

    public MovementInputSystem(Input input) {
        super(Family.all(VelocityComponent.class, PlayerControlledComponent.class).get());
        this.input = input;
        this.velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = velocityMapper.get(entity);

        // Get input direction
        Vector2f moveDir = new Vector2f();
        if (input.isKeyPressed(GLFW_KEY_W)) moveDir.y += 1;
        if (input.isKeyPressed(GLFW_KEY_S)) moveDir.y -= 1;
        if (input.isKeyPressed(GLFW_KEY_A)) moveDir.x -= 1;
        if (input.isKeyPressed(GLFW_KEY_D)) moveDir.x += 1;

        // Normalize if moving diagonally
        if (moveDir.lengthSquared() > 0) {
            moveDir.normalize();
        }

        // Apply acceleration
        moveDir.mul(velocity.getAcceleration() * deltaTime);
        Vector2f currentVel = velocity.getVelocity();
        currentVel.add(moveDir);
    }
} 