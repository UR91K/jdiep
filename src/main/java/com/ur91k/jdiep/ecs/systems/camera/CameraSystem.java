package com.ur91k.jdiep.ecs.systems.camera;

import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.core.time.Time;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.components.camera.CameraTargetComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
@SuppressWarnings("unused")
public class CameraSystem extends System {
    private static final Logger logger = Logger.getLogger(CameraSystem.class);
    private static final float FREE_ROAM_SPEED = 500.0f; // pixels per second
    private final Input input;

    public CameraSystem(Input input) {
        this.input = input;
    }

    @Override
    public void update() {
        var cameras = world.getEntitiesWith(CameraComponent.class);
        if (cameras.isEmpty()) return;

        double deltaTime = Time.getDeltaTime();

        for (Entity cameraEntity : cameras) {
            CameraComponent camera = cameraEntity.getComponent(CameraComponent.class);
            
            switch (camera.getMode()) {
                case FOLLOW -> updateFollowMode(camera, deltaTime);
                case FREE_ROAM -> updateFreeRoamMode(camera, deltaTime);
                case SPECTATE -> updateSpectateMode(camera, deltaTime);
            }

            // Handle zoom input (mouse wheel)
            float scrollY = input.getScrollY();
            if (scrollY != 0) {
                float newZoom = camera.getZoom() * (1 + scrollY * 0.1f);
                newZoom = Math.max(0.1f, Math.min(5.0f, newZoom)); // Clamp zoom
                camera.setZoom(newZoom);
            }
        }
    }

    private void updateFollowMode(CameraComponent camera, double deltaTime) {
        var targets = world.getEntitiesWith(CameraTargetComponent.class, TransformComponent.class);
        if (targets.isEmpty()) return;

        // Follow the first target found
        Entity target = targets.iterator().next();
        TransformComponent targetTransform = target.getComponent(TransformComponent.class);
        Vector2f targetPos = targetTransform.getPosition();
        Vector2f cameraPos = camera.getPosition();
        Vector2f velocity = camera.getVelocity();

        // Calculate spring force (F = -kx)
        float dx = cameraPos.x - targetPos.x;
        float dy = cameraPos.y - targetPos.y;

        // Apply spring force
        float fx = -camera.getSpringStiffness() * dx;
        float fy = -camera.getSpringStiffness() * dy;

        // Apply damping force (F = -cv)
        fx -= camera.getDamping() * velocity.x;
        fy -= camera.getDamping() * velocity.y;

        // Update velocity (a = F/m, assuming mass = 1)
        velocity.x += fx * deltaTime;
        velocity.y += fy * deltaTime;

        // Update position
        cameraPos.x += velocity.x * deltaTime;
        cameraPos.y += velocity.y * deltaTime;

        // Stop movement if very slow
        if (velocity.length() < 0.01f) {
            velocity.zero();
        }
    }

    private void updateFreeRoamMode(CameraComponent camera, double deltaTime) {
        Vector2f moveDir = new Vector2f(0, 0);
        
        // WASD movement
        if (input.isKeyPressed(GLFW_KEY_W)) moveDir.y += 1;
        if (input.isKeyPressed(GLFW_KEY_S)) moveDir.y -= 1;
        if (input.isKeyPressed(GLFW_KEY_A)) moveDir.x -= 1;
        if (input.isKeyPressed(GLFW_KEY_D)) moveDir.x += 1;

        // Normalize if moving diagonally
        if (moveDir.lengthSquared() > 0) {
            moveDir.normalize();
        }

        // Update position directly (no physics in free roam)
        camera.getPosition().add(
            new Vector2f(moveDir).mul((float)(FREE_ROAM_SPEED * deltaTime))
        );
        camera.getVelocity().zero(); // No velocity in free roam mode
    }

    private void updateSpectateMode(CameraComponent camera, double deltaTime) {
        // For now, spectate mode behaves the same as follow mode
        updateFollowMode(camera, deltaTime);
    }
} 