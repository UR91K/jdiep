package com.ur91k.jdiep.engine.ecs;

import com.ur91k.jdiep.engine.graphics.RenderSystem;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class EntityRenderSystem extends System {
    private final RenderSystem renderSystem;
    private final int circleVao;
    private final int circleVbo;
    private static final int CIRCLE_SEGMENTS = 32;

    public EntityRenderSystem(RenderSystem renderSystem) {
        this.renderSystem = renderSystem;

        // Create circle mesh
        float[] circleVertices = new float[CIRCLE_SEGMENTS * 2];
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            float angle = (float) (2.0f * Math.PI * i / CIRCLE_SEGMENTS);
            circleVertices[i * 2] = (float) Math.cos(angle);
            circleVertices[i * 2 + 1] = (float) Math.sin(angle);
        }

        // Upload circle mesh to GPU
        circleVao = glGenVertexArrays();
        circleVbo = glGenBuffers();
        
        glBindVertexArray(circleVao);
        glBindBuffer(GL_ARRAY_BUFFER, circleVbo);
        glBufferData(GL_ARRAY_BUFFER, circleVertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    }

    @Override
    public void update() {
        List<Entity> renderableEntities = world.getEntitiesWith(
            TransformComponent.class,
            ShapeComponent.class,
            ColorComponent.class
        );

        for (Entity entity : renderableEntities) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            ShapeComponent shape = entity.getComponent(ShapeComponent.class);
            ColorComponent color = entity.getComponent(ColorComponent.class);

            if (shape.getType() == ShapeComponent.ShapeType.CIRCLE) {
                renderCircle(transform, shape, color);
            }
        }
    }

    private void renderCircle(TransformComponent transform, ShapeComponent shape, ColorComponent color) {
        Matrix4f model = new Matrix4f()
            .translate(transform.getPosition().x, transform.getPosition().y, 0)
            .rotate(transform.getRotation(), 0, 0, 1)
            .scale(shape.getRadius() * transform.getScale().x, 
                  shape.getRadius() * transform.getScale().y, 
                  1);

        glBindVertexArray(circleVao);
        
        // Draw filled circle
        renderSystem.setTransformAndColor(model, color.getFillColor());
        glDrawArrays(GL_TRIANGLE_FAN, 0, CIRCLE_SEGMENTS);
        
        // Draw outline
        renderSystem.setTransformAndColor(model, color.getOutlineColor());
        glLineWidth(color.getOutlineThickness());
        glDrawArrays(GL_LINE_LOOP, 0, CIRCLE_SEGMENTS);
    }

    public void cleanup() {
        glDeleteBuffers(circleVbo);
        glDeleteVertexArrays(circleVao);
    }
} 