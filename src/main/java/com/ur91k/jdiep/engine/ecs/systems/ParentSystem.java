package com.ur91k.jdiep.engine.ecs.systems;

import com.ur91k.jdiep.engine.core.logging.Logger;
import com.ur91k.jdiep.engine.ecs.components.ParentComponent;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.System;

import org.joml.Vector2f;

public class ParentSystem extends System {
    private static final Logger logger = Logger.getLogger(ParentSystem.class);

    @Override
    public void update() {
        var children = world.getEntitiesWith(
            ParentComponent.class,
            TransformComponent.class
        );

        for (Entity child : children) {
            ParentComponent parentComp = child.getComponent(ParentComponent.class);
            TransformComponent childTransform = child.getComponent(TransformComponent.class);
            Entity parent = parentComp.getParent();
            
            if (parent == null) {
                logger.warn("Child entity {} has null parent", child.getId());
                continue;
            }
            
            TransformComponent parentTransform = parent.getComponent(TransformComponent.class);
            if (parentTransform == null) {
                logger.warn("Parent entity {} has no transform component", parent.getId());
                continue;
            }

            // Get parent's world position and rotation
            Vector2f parentPos = parentTransform.getPosition();
            float parentRot = parentTransform.getRotation();
            
            // Get local offset and rotation
            Vector2f localOffset = parentComp.getLocalOffset();
            float localRot = parentComp.getLocalRotation();
            
            // Calculate rotated offset
            float cos = (float)Math.cos(parentRot);
            float sin = (float)Math.sin(parentRot);
            Vector2f rotatedOffset = new Vector2f(
                localOffset.x * cos - localOffset.y * sin,
                localOffset.x * sin + localOffset.y * cos
            );
            
            // Update child transform
            Vector2f worldPos = new Vector2f(parentPos).add(rotatedOffset);
            childTransform.setPosition(worldPos);
            childTransform.setRotation(parentRot + localRot);
            
            logger.trace("Updated child {} transform - pos: {}, rot: {}", 
                child.getId(), worldPos, parentRot + localRot);
        }
    }
} 