package com.ur91k.jdiep.engine.ecs.systems.movement;

import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.systems.core.InputSystem;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MovementInputSystem extends InputSystem {
    
    public MovementInputSystem(World world, Input input) {
        super(world, input);
    }
    
    @Override
    public void update() {
        for (Entity entity : world.getEntitiesWith(
            MovementComponent.class, 
            PlayerControlledComponent.class
        )) {
            handlePlayerInput(entity);
        }
    }
    
    private void handlePlayerInput(Entity entity) {
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        Vector2f inputDir = getInputDirection();
        
        // Normalize if moving diagonally
        if (inputDir.length() > 0) {
            inputDir.normalize();
            inputDir.mul(movement.getMoveSpeed());
        }
        
        // Set acceleration instead of velocity directly
        movement.setAcceleration(inputDir);
    }
    
    private Vector2f getInputDirection() {
        float x = 0.0f;
        float y = 0.0f;
        
        if (input.isKeyPressed(GLFW_KEY_W)) y += 1.0f;
        if (input.isKeyPressed(GLFW_KEY_S)) y -= 1.0f;
        if (input.isKeyPressed(GLFW_KEY_A)) x -= 1.0f;
        if (input.isKeyPressed(GLFW_KEY_D)) x += 1.0f;
        
        return new Vector2f(x, y);
    }
} 