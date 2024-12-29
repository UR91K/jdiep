package com.ur91k.jdiep.ecs.components.camera;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

public class CameraTargetComponent implements Component {
    // Marker component - no data needed
    
    /**
     * Helper method to ensure only one entity has this component
     */
    public static void setTarget(Engine engine, Entity targetEntity) {
        // Remove component from any existing entities
        ImmutableArray<Entity> targets = engine.getEntitiesFor(
            Family.all(CameraTargetComponent.class).get()
        );
        
        for (Entity entity : targets) {
            entity.remove(CameraTargetComponent.class);
        }
        
        // Add component to new target
        targetEntity.add(new CameraTargetComponent());
    }
} 