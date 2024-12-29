package com.ur91k.jdiep.ecs.systems.camera;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.common.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.tinylog.Logger;

public class CameraSystem extends IteratingSystem {
    private final ComponentMapper<CameraComponent> cameraMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final Input input;
    private static final float MIN_VELOCITY = 0.01f;  // Velocity threshold for stopping

    public CameraSystem(Input input) {
        super(Family.all(CameraComponent.class, TransformComponent.class).get());
        
        this.input = input;
        this.cameraMapper = ComponentMapper.getFor(CameraComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
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
            if (target != null) {
                Vector2f targetPos;
                
                // Try to get position from physics body first
                if (collisionMapper.has(target)) {
                    CollisionComponent collision = collisionMapper.get(target);
                    if (collision.getBody() != null) {
                        Vec2 bodyPos = collision.getBody().getPosition();
                        targetPos = new Vector2f(bodyPos.x, bodyPos.y);
                    } else {
                        targetPos = transformMapper.get(target).getPosition();
                    }
                } else {
                    targetPos = transformMapper.get(target).getPosition();
                }
                
                Vector2f cameraPos = transform.getPosition();
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
                if (velocity.length() < MIN_VELOCITY) {
                    velocity.zero();
                }
                
                transform.setPosition(cameraPos);
            }
        }
    }
} 