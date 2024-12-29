package com.ur91k.jdiep.ecs.systems.camera;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class CameraSystem extends IteratingSystem {
    private final ComponentMapper<CameraComponent> cameraMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final Input input;

    public CameraSystem(Input input) {
        super(Family.all(CameraComponent.class, TransformComponent.class).get());
        
        this.input = input;
        this.cameraMapper = ComponentMapper.getFor(CameraComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CameraComponent camera = cameraMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);

        // Handle zoom input
        float scrollY = input.getScrollY();
        if (scrollY != 0) {
            camera.adjustZoom(-scrollY);  // Invert scroll for natural zoom feel
        }

        // Update position if following a target
        if (camera.hasTarget()) {
            Entity target = camera.getTarget();
            if (target != null && transformMapper.has(target)) {
                Vector2f targetPos = transformMapper.get(target).getPosition();
                Vector2f currentPos = transform.getPosition();
                
                // Smoothly interpolate to target position
                float lerpFactor = camera.getLerpFactor();
                currentPos.lerp(targetPos, lerpFactor * deltaTime * 60.0f);  // Scale by 60 for frame-rate independence
                transform.setPosition(currentPos);
            }
        }
        
        // Update view matrix
        Matrix4f viewMatrix = new Matrix4f()
            .translate(-transform.getPosition().x, -transform.getPosition().y, 0)
            .scale(camera.getZoom());
            
        // Pass view matrix to input system
        input.setViewMatrix(viewMatrix);
    }
} 