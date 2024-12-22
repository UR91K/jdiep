package com.ur91k.jdiep.engine.ecs;

import org.joml.Vector2f;

public class ParentSystem extends System {
    @Override
    public void update() {
        // Get all entities with both transform and parent components
        var children = world.getEntitiesWith(
            TransformComponent.class,
            ParentComponent.class
        );

        for (Entity child : children) {
            ParentComponent parentComp = child.getComponent(ParentComponent.class);
            TransformComponent childTransform = child.getComponent(TransformComponent.class);
            TransformComponent parentTransform = parentComp.getParent().getComponent(TransformComponent.class);

            // Calculate world position based on parent's transform and local offset
            Vector2f localOffset = parentComp.getLocalOffset();
            float parentRotation = parentTransform.getRotation();
            
            // Rotate offset by parent's rotation
            float rotatedX = (float) (localOffset.x * Math.cos(parentRotation) - 
                                    localOffset.y * Math.sin(parentRotation));
            float rotatedY = (float) (localOffset.x * Math.sin(parentRotation) + 
                                    localOffset.y * Math.cos(parentRotation));

            // Set position relative to parent
            childTransform.setPosition(new Vector2f(
                parentTransform.getPosition().x + rotatedX,
                parentTransform.getPosition().y + rotatedY
            ));

            // Set rotation relative to parent
            childTransform.setRotation(parentRotation + parentComp.getLocalRotation());
        }
    }
} 