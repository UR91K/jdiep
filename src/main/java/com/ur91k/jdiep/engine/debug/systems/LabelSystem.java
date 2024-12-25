package com.ur91k.jdiep.engine.debug.systems;

import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.engine.debug.components.LabelComponent;
import com.ur91k.jdiep.engine.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.System;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.graphics.text.TextRenderer;

import org.joml.Vector2f;

import java.util.Collection;

public class LabelSystem extends System {
    private static final Logger logger = Logger.getLogger(LabelSystem.class);
    private final TextRenderer textRenderer;
    private boolean debugMode = false;

    public LabelSystem(World world, TextRenderer textRenderer) {
        setWorld(world);
        this.textRenderer = textRenderer;
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
            
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            Vector2f position = new Vector2f(transform.getPosition()).add(label.getOffset());
            
            String text = label.getText();
            if (text == null || text.isEmpty()) continue;
            
            textRenderer.renderText(
                text,
                position,
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