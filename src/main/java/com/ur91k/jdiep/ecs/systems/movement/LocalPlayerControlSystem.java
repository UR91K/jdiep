package com.ur91k.jdiep.ecs.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankControllerComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class LocalPlayerControlSystem extends IteratingSystem {
    private final Input input;
    private final ComponentMapper<TankControllerComponent> controllerMapper;
    private final ComponentMapper<TransformComponent> transformMapper;

    public LocalPlayerControlSystem(Input input) {
        super(Family.all(
            PlayerControlledComponent.class,
            TankControllerComponent.class,
            TransformComponent.class
        ).get());
        
        this.input = input;
        this.controllerMapper = ComponentMapper.getFor(TankControllerComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TankControllerComponent controller = controllerMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        // Update movement direction from WASD input
        Vector2f moveDir = new Vector2f();
        if (input.isKeyPressed(GLFW_KEY_W)) moveDir.y += 1;
        if (input.isKeyPressed(GLFW_KEY_S)) moveDir.y -= 1;
        if (input.isKeyPressed(GLFW_KEY_A)) moveDir.x -= 1;
        if (input.isKeyPressed(GLFW_KEY_D)) moveDir.x += 1;
        controller.setMoveDirection(moveDir);
        
        // Update aim angle from mouse position
        Vector2f mousePos = input.getWorldMousePosition();
        Vector2f entityPos = transform.getPosition();
        Vector2f direction = new Vector2f(mousePos).sub(entityPos);
        float aimAngle = (float) Math.atan2(direction.y, direction.x);
        controller.setAimAngle(aimAngle);
        
        // Update shooting state from mouse input
        controller.setShooting(input.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));
    }
} 