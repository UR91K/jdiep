package com.ur91k.jdiep.graphics.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class OpenGLRenderer implements Renderer {
    private final ShaderProgram shader;
    private final Matrix4f projection;
    private final Matrix4f view;
    private final int vao;
    private final int vbo;

    public OpenGLRenderer(int windowWidth, int windowHeight) {
        // Initialize matrices
        this.projection = new Matrix4f().ortho(
            -windowWidth/2.0f, windowWidth/2.0f,
            -windowHeight/2.0f, windowHeight/2.0f,
            -1, 1
        );
        this.view = new Matrix4f();

        // Load shader
        ClassLoader classLoader = getClass().getClassLoader();
        this.shader = new ShaderProgram(
            classLoader.getResourceAsStream("shaders/game_vertex.glsl"),
            classLoader.getResourceAsStream("shaders/game_fragment.glsl")
        );

        // Create VAO/VBO for shape rendering
        this.vao = glGenVertexArrays();
        this.vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 1024 * Float.BYTES, GL_DYNAMIC_DRAW); // Allocate buffer
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    }

    @Override
    public void drawCircle(Vector2f position, float radius, Vector4f color) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f().translate(position.x, position.y, 0));
        shader.setVector4f("color", color);

        // Generate circle vertices
        int segments = 32;
        FloatBuffer circleBuffer = BufferUtils.createFloatBuffer(segments * 2);
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2.0f * Math.PI * i / segments);
            circleBuffer.put((float) Math.cos(angle) * radius);
            circleBuffer.put((float) Math.sin(angle) * radius);
        }
        circleBuffer.flip();

        // Draw circle
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, circleBuffer);
        glDrawArrays(GL_LINE_LOOP, 0, segments);
    }

    @Override
    public void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f()
            .translate(position.x, position.y, 0)
            .rotate(rotation, 0, 0, 1)
        );
        shader.setVector4f("color", color);

        // Generate rectangle vertices
        FloatBuffer rectBuffer = BufferUtils.createFloatBuffer(8);
        float halfWidth = dimensions.x / 2;
        float halfHeight = dimensions.y / 2;
        float[] vertices = new float[] {
            -halfWidth, -halfHeight,
            halfWidth, -halfHeight,
            halfWidth, halfHeight,
            -halfWidth, halfHeight
        };
        rectBuffer.put(vertices).flip();

        // Draw rectangle
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, rectBuffer);
        glDrawArrays(GL_LINE_LOOP, 0, 4);
    }

    @Override
    public void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f()
            .translate(position.x, position.y, 0)
            .rotate(rotation, 0, 0, 1)
        );
        shader.setVector4f("color", color);

        // Convert vertices to buffer
        FloatBuffer polyBuffer = BufferUtils.createFloatBuffer(vertices.length * 2);
        for (Vector2f vertex : vertices) {
            polyBuffer.put(vertex.x);
            polyBuffer.put(vertex.y);
        }
        polyBuffer.flip();

        // Draw polygon
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, polyBuffer);
        glDrawArrays(GL_LINE_LOOP, 0, vertices.length);
    }

    public void setView(Matrix4f view) {
        this.view.set(view);
    }

    public void cleanup() {
        shader.cleanup();
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
} 