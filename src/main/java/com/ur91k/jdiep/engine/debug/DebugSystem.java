package com.ur91k.jdiep.engine.debug;

import com.ur91k.jdiep.engine.ecs.World;
import com.ur91k.jdiep.engine.ecs.systems.base.GameSystem;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.components.debug.DebugStateComponent;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.ecs.components.MovementComponent;
import java.util.Map;

public class DebugSystem extends GameSystem {
    private DebugOverlay debugOverlay;
    
    public DebugSystem(World world, DebugOverlay debugOverlay) {
        super(world, true);  // Debug system is client-only
        this.debugOverlay = debugOverlay;
    }
    
    @Override
    public void update() {
        if (!debugOverlay.isVisible()) return;
        
        for (Entity entity : world.getEntitiesWith(DebugStateComponent.class)) {
            updateDebugInfo(entity);
        }
    }
    
    private void updateDebugInfo(Entity entity) {
        DebugStateComponent debug = entity.getComponent(DebugStateComponent.class);
        if (!debug.isVisible()) return;
        
        // Update basic entity info
        debug.setValue("entityId", entity.getId());
        
        // Update transform info if present
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        if (transform != null) {
            debug.setValue("position", transform.getPosition().toString());
            debug.setValue("rotation", String.format("%.2f", transform.getRotation()));
        }
        
        // Update movement info if present
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        if (movement != null) {
            debug.setValue("velocity", movement.getVelocity().toString());
            debug.setValue("speed", String.format("%.2f", movement.getVelocity().length()));
        }
        
        // Add debug info to overlay
        Map<String, Object> values = debug.getValues();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            debugOverlay.setInfo(
                String.format("Entity %d - %s", entity.getId(), entry.getKey()),
                entry.getValue().toString()
            );
        }
    }
} 