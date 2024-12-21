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
    private final int polygonVao;
    private final int polygonVbo;
    private static final int CIRCLE_SEGMENTS = 32;
    private static final int MAX_POLYGON_VERTICES = 8;  // Support up to octagons

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

        // Create polygon VAO/VBO (will be updated per-polygon)
        polygonVao = glGenVertexArrays();
        polygonVbo = glGenBuffers();
        
        glBindVertexArray(polygonVao);
        glBindBuffer(GL_ARRAY_BUFFER, polygonVbo);
        glBufferData(GL_ARRAY_BUFFER, MAX_POLYGON_VERTICES * 2 * Float.BYTES, GL_DYNAMIC_DRAW);
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

            switch (shape.getType()) {
                case CIRCLE -> renderCircle(transform, shape, color);
                case TRIANGLE, POLYGON -> renderPolygon(transform, shape, color);
                default -> {} // Ignore unsupported shapes for now
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

    private void renderPolygon(TransformComponent transform, ShapeComponent shape, ColorComponent color) {
        float[] vertices;
        int vertexCount;

        if (shape.getType() == ShapeComponent.ShapeType.POLYGON && shape.getVertices() != null) {
            // Use custom vertices if provided
            vertices = shape.getVertices();
            vertexCount = vertices.length / 2;
        } else {
            // Generate regular polygon vertices
            vertexCount = shape.getSides();
            vertices = new float[vertexCount * 2];
            
            for (int i = 0; i < vertexCount; i++) {
                float angle = (float) (2.0f * Math.PI * i / vertexCount + Math.PI / 2);  // Start at top
                vertices[i * 2] = (float) Math.cos(angle);
                vertices[i * 2 + 1] = (float) Math.sin(angle);
            }
        }

        // Upload vertices
        glBindVertexArray(polygonVao);
        glBindBuffer(GL_ARRAY_BUFFER, polygonVbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Create transformation matrix
        Matrix4f model = new Matrix4f()
            .translate(transform.getPosition().x, transform.getPosition().y, 0)
            .rotate(transform.getRotation(), 0, 0, 1)
            .scale(shape.getRadius() * transform.getScale().x, 
                  shape.getRadius() * transform.getScale().y, 
                  1);

        // Draw filled polygon
        renderSystem.setTransformAndColor(model, color.getFillColor());
        glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

        // Draw outline
        renderSystem.setTransformAndColor(model, color.getOutlineColor());
        glLineWidth(color.getOutlineThickness());
        glDrawArrays(GL_LINE_LOOP, 0, vertexCount);
    }

    public void cleanup() {
        glDeleteBuffers(circleVbo);
        glDeleteVertexArrays(circleVao);
        glDeleteBuffers(polygonVbo);
        glDeleteVertexArrays(polygonVao);
    }
} 