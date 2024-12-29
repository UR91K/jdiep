package com.ur91k.jdiep.ecs.systems.debug;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.debug.LabelComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.text.TextRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LabelSystem extends IteratingSystem {
    private final TextRenderer textRenderer;
    private final Matrix4f viewMatrix;
    private final Vector2f windowSize;
    
    private final ComponentMapper<LabelComponent> labelMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    
    public LabelSystem(TextRenderer textRenderer, Matrix4f viewMatrix, Vector2f windowSize) {
        super(Family.all(LabelComponent.class).get());
        
        this.textRenderer = textRenderer;
        this.viewMatrix = viewMatrix;
        this.windowSize = windowSize;
        
        this.labelMapper = ComponentMapper.getFor(LabelComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        LabelComponent label = labelMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        String text = label.getText();
        if (text == null || text.isEmpty()) return;
        
        Vector2f position;
        if (label.isScreenSpace()) {
            // Screen space labels don't need transform
            position = label.getOffset();
            textRenderer.renderScreenText(text, position, label.getColor(), label.getScale());
        } else if (transform != null) {
            // World space labels need to be transformed to screen space
            Vector2f worldPos = transform.getPosition();
            position = worldToScreen(worldPos);
            position.add(label.getOffset());
            textRenderer.renderText(text, position, label.getColor(), label.getScale());
        }
    }
    
    private Vector2f worldToScreen(Vector2f worldPos) {
        // Transform world position to screen coordinates
        Vector4f clipSpace = new Vector4f(worldPos.x, worldPos.y, 0, 1);
        clipSpace.mul(viewMatrix);
        
        // Convert to screen coordinates
        float screenX = (clipSpace.x + 1.0f) * 0.5f * windowSize.x;
        float screenY = (1.0f - clipSpace.y) * 0.5f * windowSize.y;
        
        return new Vector2f(screenX, screenY);
    }
    
    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix.set(viewMatrix);
    }
    
    public void setWindowSize(float width, float height) {
        this.windowSize.set(width, height);
    }
} 