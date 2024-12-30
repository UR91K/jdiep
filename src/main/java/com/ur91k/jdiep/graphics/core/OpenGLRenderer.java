package com.ur91k.jdiep.graphics.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.core.window.Input;

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
    private final int gridVao;
    private final int gridVbo;
    private static final int GRID_SIZE = 1024;
    private static final float GRID_SPACING = 26.0f;
    private static final float BASE_VIEW_HEIGHT = 720.0f;  // Base height for consistent scale
    private static final float MAX_ASPECT_RATIO = 16.0f / 9.0f;  // Maximum allowed aspect ratio
    private final Input input;
    private int windowWidth;
    private int windowHeight;
    private float aspectRatio;

    public OpenGLRenderer(int windowWidth, int windowHeight, Input input) {
        this.input = input;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        float rawAspectRatio = (float)windowWidth / windowHeight;
        
        float viewWidth, viewHeight;
        if (rawAspectRatio > MAX_ASPECT_RATIO) {
            // Window is too wide - maintain max aspect ratio by increasing view height
            viewWidth = BASE_VIEW_HEIGHT * MAX_ASPECT_RATIO;
            viewHeight = viewWidth / rawAspectRatio;
        } else {
            // Normal case - use base height and actual aspect ratio
            viewHeight = BASE_VIEW_HEIGHT;
            viewWidth = viewHeight * rawAspectRatio;
        }
        
        // Initialize projection matrix
        this.projection = new Matrix4f().ortho(
            -viewWidth/2.0f, viewWidth/2.0f,
            -viewHeight/2.0f, viewHeight/2.0f,
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
        glBufferData(GL_ARRAY_BUFFER, 1024 * Float.BYTES, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        // Create grid VAO/VBO
        this.gridVao = glGenVertexArrays();
        this.gridVbo = glGenBuffers();
        setupGrid();

        // Enable anti-aliasing
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        
        // Pass projection matrix to input system
        if (input != null) {
            input.setProjectionMatrix(projection);
        }
    }

    public void handleResize(int newWidth, int newHeight) {
        this.windowWidth = newWidth;
        this.windowHeight = newHeight;
        float rawAspectRatio = (float)newWidth / newHeight;
        
        float viewWidth, viewHeight;
        if (rawAspectRatio > MAX_ASPECT_RATIO) {
            // Window is too wide - maintain max aspect ratio by increasing view height
            viewWidth = BASE_VIEW_HEIGHT * MAX_ASPECT_RATIO;
            viewHeight = viewWidth / rawAspectRatio;
        } else {
            // Normal case - use base height and actual aspect ratio
            viewHeight = BASE_VIEW_HEIGHT;
            viewWidth = viewHeight * rawAspectRatio;
        }
        
        // Update projection matrix
        projection.identity().ortho(
            -viewWidth/2.0f, viewWidth/2.0f,
            -viewHeight/2.0f, viewHeight/2.0f,
            -1, 1
        );
        
        // Update viewport to use full window
        glViewport(0, 0, newWidth, newHeight);
        
        // Update input system's projection matrix
        if (input != null) {
            input.setProjectionMatrix(projection);
        }
    }

    private void setupGrid() {
        // Generate grid vertices
        // Each line needs 2 vertices (start and end), each vertex needs 2 floats (x,y)
        // Total lines = (GRID_SIZE + 1) * 2 (horizontal + vertical)
        int totalVertices = (GRID_SIZE + 1) * 2 * 2 * 2;  // (grid + 1) * (h+v) * verts * coords
        FloatBuffer gridBuffer = BufferUtils.createFloatBuffer(totalVertices);
        float extent = GRID_SIZE * GRID_SPACING / 2;
        
        // Vertical lines
        for (int i = 0; i <= GRID_SIZE; i++) {
            float x = -extent + i * GRID_SPACING;
            gridBuffer.put(x).put(-extent);
            gridBuffer.put(x).put(extent);
        }
        
        // Horizontal lines
        for (int i = 0; i <= GRID_SIZE; i++) {
            float y = -extent + i * GRID_SPACING;
            gridBuffer.put(-extent).put(y);
            gridBuffer.put(extent).put(y);
        }
        gridBuffer.flip();

        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        glBufferData(GL_ARRAY_BUFFER, gridBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    }

    public void drawGrid() {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f());
        shader.setVector4f("color", RenderingConstants.GRID_COLOR);

        glDisable(GL_LINE_SMOOTH);
        glLineWidth(1.0f);
        glBindVertexArray(gridVao);
        glDrawArrays(GL_LINES, 0, GRID_SIZE * 4);
        glEnable(GL_LINE_SMOOTH);
    }

    @Override
    public void drawCircle(Vector2f position, float radius, Vector4f color) {
        drawCircle(position, radius, color, 1.0f, true);
    }

    @Override
    public void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color) {
        drawRectangle(position, dimensions, rotation, color, 1.0f, true);
    }

    @Override
    public void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color) {
        drawPolygon(position, vertices, rotation, color, 1.0f, true);
    }

    @Override
    public void drawCircle(Vector2f position, float radius, Vector4f color, float lineWidth, boolean filled) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f().translate(position.x, position.y, 0));
        shader.setVector4f("color", color);

        // Generate circle vertices
        int segments = 32;
        FloatBuffer circleBuffer = BufferUtils.createFloatBuffer((segments + 1) * 2);
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0f * Math.PI * i / segments);
            circleBuffer.put((float) Math.cos(angle) * radius);
            circleBuffer.put((float) Math.sin(angle) * radius);
        }
        circleBuffer.flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, circleBuffer);

        if (filled) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, segments + 1);
        }

        glLineWidth(lineWidth);
        glDrawArrays(GL_LINE_LOOP, 0, segments);
    }

    @Override
    public void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color, float lineWidth, boolean filled) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f()
            .translate(position.x, position.y, 0)
            .rotate(rotation, 0, 0, 1)
        );
        shader.setVector4f("color", color);

        float halfWidth = dimensions.x / 2;
        float halfHeight = dimensions.y / 2;
        float[] vertices = new float[] {
            -halfWidth, -halfHeight,
            halfWidth, -halfHeight,
            halfWidth, halfHeight,
            -halfWidth, halfHeight
        };
        FloatBuffer rectBuffer = BufferUtils.createFloatBuffer(8).put(vertices).flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, rectBuffer);

        if (filled) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        }

        glLineWidth(lineWidth);
        glDrawArrays(GL_LINE_LOOP, 0, 4);
    }

    @Override
    public void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color, float lineWidth, boolean filled) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f()
            .translate(position.x, position.y, 0)
            .rotate(rotation, 0, 0, 1)
        );
        shader.setVector4f("color", color);

        FloatBuffer polyBuffer = BufferUtils.createFloatBuffer(vertices.length * 2);
        for (Vector2f vertex : vertices) {
            polyBuffer.put(vertex.x);
            polyBuffer.put(vertex.y);
        }
        polyBuffer.flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, polyBuffer);

        if (filled) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, vertices.length);
        }

        glLineWidth(lineWidth);
        glDrawArrays(GL_LINE_LOOP, 0, vertices.length);
    }

    public void setView(Matrix4f view) {
        this.view.set(view);
    }

    public void cleanup() {
        shader.cleanup();
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(gridVbo);
        glDeleteVertexArrays(gridVao);
    }
} 