package com.ur91k.jdiep.ecs.systems.transform;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;

public class ParentSystem extends IteratingSystem {
    private ComponentMapper<ParentComponent> parentMapper;
    private ComponentMapper<TransformComponent> transformMapper;

    public ParentSystem() {
        super(Family.all(ParentComponent.class, TransformComponent.class).get());
        
        parentMapper = ComponentMapper.getFor(ParentComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParentComponent parentComp = parentMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        Entity parent = parentComp.getParent();
        
        // If no parent or parent has no transform, use local values
        if (parent == null || !transformMapper.has(parent)) {
            transform.setPosition(parentComp.getLocalOffset());
            transform.setRotation(parentComp.getLocalRotation());
            return;
        }

        // Get parent's transform
        TransformComponent parentTransform = transformMapper.get(parent);
        
        // Update position and rotation based on parent
        transform.setPosition(parentComp.getWorldPosition(parentTransform));
        transform.setRotation(parentComp.getWorldRotation(parentTransform));
    }
} 