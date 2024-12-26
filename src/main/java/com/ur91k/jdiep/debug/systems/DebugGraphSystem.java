package com.ur91k.jdiep.debug.systems;

import com.ur91k.jdiep.debug.components.core.DebugGraphComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.ecs.systems.render.RenderSystem;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;

public class DebugGraphSystem extends System {
    private final RenderSystem renderSystem;
    private boolean debugMode = false;
    private static final float GRAPH_LINE_THICKNESS = 1.0f;
    private static final Vector4f BACKGROUND_COLOR = new Vector4f(0, 0, 0, 0.5f);
    private static final Vector4f GRID_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 0.5f);

    public DebugGraphSystem(World world, RenderSystem renderSystem) {
        setWorld(world);
        this.renderSystem = renderSystem;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }

    @Override
    public void update() {
        if (!debugMode) return;

        // Store current line smooth state
        boolean lineSmooth = glIsEnabled(GL_LINE_SMOOTH);
        
        // Disable line smoothing for crisp graph lines
        glDisable(GL_LINE_SMOOTH);

        Collection<Entity> entities = world.getEntitiesWith(DebugGraphComponent.class);
        
        for (Entity entity : entities) {
            DebugGraphComponent graph = entity.getComponent(DebugGraphComponent.class);
            if (!graph.isVisible()) continue;
            
            renderGraph(graph);
        }
        
        // Restore previous line smooth state
        if (lineSmooth) {
            glEnable(GL_LINE_SMOOTH);
        }
    }

    private void renderGraph(DebugGraphComponent graph) {
        Vector2f pos = graph.getFixedScreenPosition();
        int width = graph.getWidth();
        int height = graph.getHeight();
        
        // Draw background using fixed coordinates
        renderSystem.drawFixedRect(
            pos,
            width,
            height,
            BACKGROUND_COLOR,
            GRAPH_LINE_THICKNESS
        );
        
        // Draw grid lines using fixed pixel spacing
        int gridLines = 4;
        float gridSpacing = height / (float)gridLines;
        for (int i = 1; i < gridLines; i++) {
            float y = pos.y + i * gridSpacing;
            renderSystem.drawFixedLine(
                new Vector2f(pos.x, y),
                new Vector2f(pos.x + width, y),
                GRID_COLOR,
                GRAPH_LINE_THICKNESS
            );
        }
        
        // Draw data points using fixed pixel spacing
        float[] values = graph.getValues();
        int currentIndex = graph.getCurrentIndex();
        float minValue = graph.getMinValue();
        float maxValue = graph.getMaxValue();
        float valueRange = maxValue - minValue;
        
        if (valueRange <= 0) return;  // Avoid division by zero
        
        // Draw lines between points using fixed coordinates
        float xStep = width / (float)(values.length - 1);
        Vector2f prevPoint = null;
        boolean prevPointValid = false;
        
        for (int i = 0; i < values.length; i++) {
            int idx = (currentIndex - values.length + i + values.length) % values.length;
            float value = values[idx];
            
            // Skip points outside the range
            if (value < minValue || value > maxValue) {
                prevPointValid = false;
                continue;
            }
            
            float normalizedValue = (value - minValue) / valueRange;
            Vector2f point = new Vector2f(
                pos.x + i * xStep,
                pos.y + height - (normalizedValue * height)
            );
            
            if (prevPointValid && prevPoint != null) {
                renderSystem.drawFixedLine(
                    prevPoint,
                    point,
                    graph.getColor(),
                    GRAPH_LINE_THICKNESS
                );
            }
            
            prevPoint = point;
            prevPointValid = true;
        }
    }
}