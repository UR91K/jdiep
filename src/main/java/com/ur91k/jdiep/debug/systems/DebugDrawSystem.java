package com.ur91k.jdiep.debug.systems;

import com.ur91k.jdiep.debug.components.DebugDrawComponent;
import com.ur91k.jdiep.debug.shapes.DebugCircle;
import com.ur91k.jdiep.debug.shapes.DebugLine;
import com.ur91k.jdiep.debug.shapes.DebugShape;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.ecs.systems.render.RenderSystem;
import com.ur91k.jdiep.core.logging.Logger;

import java.util.Collection;

public class DebugDrawSystem extends System {
    private static final Logger logger = Logger.getLogger(DebugDrawSystem.class);
    private final RenderSystem renderSystem;
    private boolean debugMode = false;

    public DebugDrawSystem(World world, RenderSystem renderSystem) {
        setWorld(world);
        this.renderSystem = renderSystem;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
        logger.debug("Debug draw mode: {}", enabled);
    }

    @Override
    public void update() {
        if (!debugMode) return;

        Collection<Entity> entities = world.getEntitiesWith(DebugDrawComponent.class);
        logger.trace("Found {} entities with DebugDrawComponent", entities.size());
        
        for (Entity entity : entities) {
            DebugDrawComponent debugDraw = entity.getComponent(DebugDrawComponent.class);
            if (!debugDraw.isVisible()) continue;

            debugDraw.update(); // Update dynamic shapes
            
            logger.trace("Entity {} has {} debug shapes", entity.getId(), debugDraw.getShapes().size());

            for (DebugShape shape : debugDraw.getShapes()) {
                if (shape instanceof DebugLine line) {
                    logger.trace("Drawing line from {} to {}", line.getStart(), line.getEnd());
                    renderLine(line, debugDraw.isScreenSpace());
                } else if (shape instanceof DebugCircle circle) {
                    logger.trace("Drawing circle at {} with radius {}", circle.getCenter(), circle.getRadius());
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