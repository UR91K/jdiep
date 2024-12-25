package com.ur91k.jdiep.engine.ecs.systems.debug;

import com.ur91k.jdiep.engine.debug.components.DebugDrawComponent;
import com.ur91k.jdiep.engine.debug.shapes.DebugCircle;
import com.ur91k.jdiep.engine.debug.shapes.DebugLine;
import com.ur91k.jdiep.engine.debug.shapes.DebugShape;
import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.System;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.systems.RenderSystem;

import java.util.Collection;

public class DebugDrawSystem extends System {
    private final RenderSystem renderSystem;
    private boolean debugMode = false;

    public DebugDrawSystem(World world, RenderSystem renderSystem) {
        setWorld(world);
        this.renderSystem = renderSystem;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }

    @Override
    public void update() {
        if (!debugMode) return;

        Collection<Entity> entities = world.getEntitiesWith(DebugDrawComponent.class);
        
        for (Entity entity : entities) {
            DebugDrawComponent debugDraw = entity.getComponent(DebugDrawComponent.class);
            if (!debugDraw.isVisible()) continue;

            debugDraw.update(); // Update dynamic shapes

            for (DebugShape shape : debugDraw.getShapes()) {
                if (shape instanceof DebugLine line) {
                    renderLine(line, debugDraw.isScreenSpace());
                } else if (shape instanceof DebugCircle circle) {
                    renderCircle(circle, debugDraw.isScreenSpace());
                }
            }
        }
    }

    private void renderLine(DebugLine line, boolean screenSpace) {
        if (screenSpace) {
            renderSystem.drawScreenLine(
                line.getStart(),
                line.getEnd(),
                line.getColor(),
                line.getThickness()
            );
        } else {
            renderSystem.drawLine(
                line.getStart(),
                line.getEnd(),
                line.getColor(),
                line.getThickness()
            );
        }
    }

    private void renderCircle(DebugCircle circle, boolean screenSpace) {
        if (screenSpace) {
            renderSystem.drawScreenCircle(
                circle.getCenter(),
                circle.getRadius(),
                circle.getColor(),
                circle.getThickness()
            );
        } else {
            renderSystem.drawCircle(
                circle.getCenter(),
                circle.getRadius(),
                circle.getColor(),
                circle.getThickness()
            );
        }
    }
} 