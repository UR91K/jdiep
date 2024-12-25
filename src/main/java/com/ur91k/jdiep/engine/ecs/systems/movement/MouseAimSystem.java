package com.ur91k.jdiep.engine.ecs.systems.movement;

import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankBodyComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;

import org.joml.Vector2f;

public class MouseAimSystem extends System {
    private static final Logger logger = Logger.getLogger(MouseAimSystem.class);
    private final Input input;

    public MouseAimSystem(Input input) {
        this.input = input;
    }
    
    @SuppressWarnings("unused")
    @Override
    public void update() {
        // Update player-controlled tanks
        var tanks = world.getEntitiesWith(
            PlayerControlledComponent.class,
            TransformComponent.class,
            TankBodyComponent.class
        );
        
        for (Entity tank : tanks) {
            TransformComponent transform = tank.getComponent(TransformComponent.class);
            TankBodyComponent body = tank.getComponent(TankBodyComponent.class);
            
            // Get mouse position in world coordinates
            Vector2f mouseWorldPos = input.getMouseWorldPosition();
            Vector2f tankPos = transform.getPosition();
            
            // Calculate direction from tank to mouse
            Vector2f direction = new Vector2f(
                mouseWorldPos.x - tankPos.x,
                mouseWorldPos.y - tankPos.y
            );
            
            // Calculate angle in radians
            float angle = (float) Math.atan2(direction.y, direction.x);
            
            // Update tank body rotation
            transform.setRotation(angle);
            logger.trace("Tank {} rotation updated to {} radians", tank.getId(), angle);
        }
    }
} 