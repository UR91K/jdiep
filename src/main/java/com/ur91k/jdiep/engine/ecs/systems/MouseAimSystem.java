package com.ur91k.jdiep.engine.ecs.systems;

import com.ur91k.jdiep.engine.core.Input;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.ecs.components.MouseAimComponent;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.systems.base.System;
import com.ur91k.jdiep.engine.ecs.components.ParentComponent;

import org.joml.Vector2f;

public class MouseAimSystem extends System {
    private static final Logger logger = Logger.getLogger(MouseAimSystem.class);
    private final Input input;

    public MouseAimSystem(Input input) {
        this.input = input;
    }

    @Override
    public void update() {
        for (Entity entity : world.getEntitiesWith(MouseAimComponent.class)) {
            MouseAimComponent mouseAim = entity.getComponent(MouseAimComponent.class);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            
            if (transform == null) {
                logger.warn("Entity {} has MouseAimComponent but no TransformComponent", entity);
                continue;
            }

            // Get mouse position in world coordinates
            Vector2f mouseWorldPos = input.getMouseWorldPosition();
            Vector2f entityPos = transform.getPosition();
            
            logger.debug("Entity position: {}", entityPos);
            logger.debug("Mouse world position: {}", mouseWorldPos);

            // Calculate direction from entity to mouse
            Vector2f direction = new Vector2f(
                mouseWorldPos.x - entityPos.x,
                mouseWorldPos.y - entityPos.y
            );
            
            logger.debug("Direction vector: {}", direction);

            // Calculate angle in radians
            float angle = (float) Math.atan2(direction.y, direction.x);
            logger.debug("Calculated angle: {} radians", angle);
            
            // Update target entity rotation
            Entity target = mouseAim.getTarget();
            ParentComponent parentComp = target.getComponent(ParentComponent.class);
            if (parentComp != null) {
                // Set the local rotation relative to parent
                parentComp.setLocalRotation(angle);
                logger.debug("Updated turret local rotation to {} radians", angle);
            } else {
                logger.warn("Target entity {} has no ParentComponent", target.getId());
            }
        }
    }
} 