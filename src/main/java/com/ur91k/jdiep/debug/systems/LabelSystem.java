package com.ur91k.jdiep.debug.systems;

import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.debug.components.LabelComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.graphics.text.TextRenderer;
import com.ur91k.jdiep.ecs.systems.render.RenderSystem;

import org.joml.Vector2f;

import java.util.Collection;

public class LabelSystem extends System {
    private static final Logger logger = Logger.getLogger(LabelSystem.class);
    private static final int CHAR_WIDTH = 8;  // BDF font character width in pixels
    private static final int CHAR_HEIGHT = 16;  // BDF font character height in pixels
    private static final int VERTICAL_OFFSET = -40;  // in pixels
    
    private final TextRenderer textRenderer;
    private final RenderSystem renderSystem;
    private boolean debugMode = false;

    public LabelSystem(World world, TextRenderer textRenderer, RenderSystem renderSystem) {
        setWorld(world);
        this.textRenderer = textRenderer;
        this.renderSystem = renderSystem;
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
    }

    @Override
    public void update() {
        if (textRenderer == null) {
            logger.error("TextRenderer is null");
            return;
        }

        renderWorldSpaceLabels();
        renderScreenSpaceLabels();
    }

    private void renderWorldSpaceLabels() {
        Collection<Entity> entities = world.getEntitiesWith(LabelComponent.class, TransformComponent.class);
        
        for (Entity entity : entities) {
            LabelComponent label = entity.getComponent(LabelComponent.class);
            if (label.isScreenSpace()) continue;  // Skip screen space labels
            if (label.isDebug() && !debugMode) continue;  // Skip debug labels when debug mode is off
            
            String text = label.getText();
            if (text == null || text.isEmpty()) continue;
            
            // Calculate text dimensions
            int textWidth = text.length() * CHAR_WIDTH;
            
            // Get entity position
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            Vector2f entityPos = transform.getPosition();
            
            // Calculate centered position above entity
            Vector2f worldPos = new Vector2f(
                entityPos.x - (textWidth / 2.0f),  // Center horizontally
                entityPos.y + VERTICAL_OFFSET      // Fixed offset above
            );
            
            // Transform to screen space
            Vector2f screenPos = renderSystem.worldToScreen(worldPos);
            
            textRenderer.renderScreenText(
                text,
                screenPos,
                label.getColor(),
                label.getScale()
            );
        }
    }

    private void renderScreenSpaceLabels() {
        Collection<Entity> entities = world.getEntitiesWith(LabelComponent.class);
        
        for (Entity entity : entities) {
            LabelComponent label = entity.getComponent(LabelComponent.class);
            if (!label.isScreenSpace()) continue;  // Skip world space labels
            if (label.isDebug() && !debugMode) continue;  // Skip debug labels when debug mode is off
            
            String text = label.getText();
            if (text == null || text.isEmpty()) continue;
            
            textRenderer.renderScreenText(
                text,
                label.getOffset(),
                label.getColor(),
                label.getScale()
            );
        }
    }
}