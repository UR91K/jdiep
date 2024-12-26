package com.ur91k.jdiep.debug.systems;

import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.debug.components.visualizers.HitboxVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.InputVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.SpawnerVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.TurretVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.VelocityVisualizerComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.ecs.systems.render.RenderSystem;
import org.joml.Vector2f;

public class VisualizerSystem extends System {
    private static final Logger logger = Logger.getLogger(VisualizerSystem.class);
    private final RenderSystem renderSystem;
    private boolean debugMode = false;

    public VisualizerSystem(World world, RenderSystem renderSystem) {
        setWorld(world);
        this.renderSystem = renderSystem;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
        logger.debug("Debug visualizer mode: {}", enabled);
    }

    @Override
    public void update() {
        if (!debugMode) return;

        renderVelocityVisualizers();
        renderInputVisualizers();
        renderHitboxVisualizers();
        renderSpawnerVisualizers();
        renderTurretVisualizers();
    }

    private void renderVelocityVisualizers() {
        for (Entity entity : world.getEntitiesWith(VelocityVisualizerComponent.class, MovementComponent.class, TransformComponent.class)) {
            VelocityVisualizerComponent visualizer = entity.getComponent(VelocityVisualizerComponent.class);
            if (!visualizer.isVisible()) continue;

            MovementComponent movement = entity.getComponent(MovementComponent.class);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            Vector2f velocity = movement.getVelocity();

            if (velocity.length() > 0.01f) {
                Vector2f start = transform.getPosition();
                Vector2f end = new Vector2f(start).add(
                    new Vector2f(velocity).mul(visualizer.getScale())
                );

                renderSystem.drawLine(start, end, visualizer.getColor(), 1.0f);
            }
        }
    }

    private void renderInputVisualizers() {
        for (Entity entity : world.getEntitiesWith(InputVisualizerComponent.class, MovementComponent.class, TransformComponent.class)) {
            InputVisualizerComponent visualizer = entity.getComponent(InputVisualizerComponent.class);
            if (!visualizer.isVisible()) continue;

            MovementComponent movement = entity.getComponent(MovementComponent.class);
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            Vector2f input = movement.getInputDirection();

            if (input.length() > 0.01f) {
                Vector2f start = transform.getPosition();
                Vector2f end = new Vector2f(start).add(
                    new Vector2f(input).normalize().mul(visualizer.getLength())
                );

                renderSystem.drawLine(start, end, visualizer.getColor(), 1.0f);
            }
        }
    }

    private void renderHitboxVisualizers() {
        for (Entity entity : world.getEntitiesWith(HitboxVisualizerComponent.class, TransformComponent.class)) {
            HitboxVisualizerComponent visualizer = entity.getComponent(HitboxVisualizerComponent.class);
            if (!visualizer.isVisible()) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            renderSystem.drawCircle(
                transform.getPosition(),
                visualizer.getRadius(),
                visualizer.getColor(),
                1.0f
            );
        }
    }

    private void renderSpawnerVisualizers() {
        for (Entity entity : world.getEntitiesWith(SpawnerVisualizerComponent.class, TransformComponent.class)) {
            SpawnerVisualizerComponent visualizer = entity.getComponent(SpawnerVisualizerComponent.class);
            if (!visualizer.isVisible()) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            Vector2f position = transform.getPosition();

            // Draw detection radius first (larger circle)
            renderSystem.drawCircle(
                position,
                visualizer.getDetectionRadius(),
                visualizer.getDetectionColor(),
                1.0f
            );

            // Draw spawn radius on top (smaller circle)
            renderSystem.drawCircle(
                position,
                visualizer.getSpawnRadius(),
                visualizer.getSpawnColor(),
                1.0f
            );
        }
    }

    private void renderTurretVisualizers() {
        for (Entity entity : world.getEntitiesWith(TurretVisualizerComponent.class, TransformComponent.class)) {
            TurretVisualizerComponent visualizer = entity.getComponent(TurretVisualizerComponent.class);
            if (!visualizer.isVisible()) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            Vector2f start = transform.getPosition();
            float rotation = transform.getRotation();

            Vector2f end = new Vector2f(start).add(
                new Vector2f(
                    (float)Math.cos(rotation),
                    (float)Math.sin(rotation)
                ).mul(visualizer.getLength())
            );

            renderSystem.drawLine(start, end, visualizer.getColor(), 1.0f);
        }
    }
} 