package com.ur91k.jdiep.graphics.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.game.config.GameUnits;

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
    private static final int CIRCLE_SEGMENTS = 32;
    private static final float GRID_SPACING = GameUnits.GRID_CELL_SIZE;  // 1 meter per grid cell
    private static final float BASE_VIEW_HEIGHT = GameUnits.pixelsToMeters(720.0f);  // Convert default height to meters
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

        // Grid lines should always be 1 pixel wide
        glLineWidth(1.0f);

        glDisable(GL_LINE_SMOOTH);
        glBindVertexArray(gridVao);
        glDrawArrays(GL_LINES, 0, GRID_SIZE * 4);
        glEnable(GL_LINE_SMOOTH);
    }

    @Override
    public void drawCircle(Vector2f position, float radius, Vector4f color) {
        drawCircle(position, radius, color, GameUnits.DEFAULT_LINE_THICKNESS, true);
    }

    @Override
    public void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color) {
        drawRectangle(position, dimensions, rotation, color, GameUnits.DEFAULT_LINE_THICKNESS, true);
    }

    @Override
    public void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color) {
        drawPolygon(position, vertices, rotation, color, GameUnits.DEFAULT_LINE_THICKNESS, true);
    }

    private Vector2f getNormal(Vector2f v1, Vector2f v2) {
        float dx = v2.x - v1.x;
        float dy = v2.y - v1.y;
        float len = (float)Math.sqrt(dx * dx + dy * dy);
        if (len < 0.0001f) return new Vector2f(1, 0); // Prevent division by zero
        return new Vector2f(-dy / len, dx / len);
    }

    private Vector2f getMiterNormal(Vector2f prev, Vector2f curr, Vector2f next) {
        Vector2f n1 = getNormal(prev, curr);
        Vector2f n2 = getNormal(curr, next);
        
        // Calculate the angle between the normals
        float dot = n1.x * n2.x + n1.y * n2.y;
        float angle = (float)Math.acos(Math.max(-1.0f, Math.min(1.0f, dot)));
        
        // For very sharp angles (like in triangles), use a different approach
        if (angle < 0.5f) { // About 30 degrees
            // Use the average of the two edges' normals
            return new Vector2f(
                (n1.x + n2.x) * 0.5f,
                (n1.y + n2.y) * 0.5f
            ).normalize();
        }
        
        // Calculate miter length (1/sin(angle/2))
        float miterLength = angle < 0.0001f ? 1.0f : (float)(1.0f / Math.sin(angle / 2.0f));
        
        // Average the two normals
        Vector2f miter = new Vector2f(
            (n1.x + n2.x) * 0.5f,
            (n1.y + n2.y) * 0.5f
        );
        
        // Normalize and scale the miter vector
        float len = (float)Math.sqrt(miter.x * miter.x + miter.y * miter.y);
        if (len < 0.0001f) return n1; // Fallback to edge normal if miter is too small
        
        miter.mul(miterLength / len);
        return miter;
    }

    private void generateOutlineTriangles(FloatBuffer buffer, Vector2f[] vertices, float lineWidth) {
        float halfWidth = lineWidth / 2.0f;
        
        // Special case for triangles
        if (vertices.length == 3) {
            for (int i = 0; i < 3; i++) {
                Vector2f curr = vertices[i];
                Vector2f next = vertices[(i + 1) % 3];
                Vector2f prev = vertices[(i + 2) % 3];  // For triangles, prev is the third vertex
                
                // Calculate edge directions
                Vector2f currEdge = new Vector2f(next).sub(new Vector2f(curr));
                Vector2f prevEdge = new Vector2f(curr).sub(new Vector2f(prev));
                
                // Calculate unit normals for the edges
                Vector2f currNormal = new Vector2f(-currEdge.y, currEdge.x).normalize();
                Vector2f prevNormal = new Vector2f(-prevEdge.y, prevEdge.x).normalize();
                
                // Calculate miter by averaging the normals
                Vector2f miter = new Vector2f(
                    (prevNormal.x + currNormal.x),
                    (prevNormal.y + currNormal.y)
                ).normalize();
                
                // Calculate next vertex's miter
                Vector2f nextEdge = new Vector2f(vertices[(i + 2) % 3]).sub(new Vector2f(next));
                Vector2f nextNormal = new Vector2f(-nextEdge.y, nextEdge.x).normalize();
                Vector2f nextMiter = new Vector2f(
                    (currNormal.x + nextNormal.x),
                    (currNormal.y + nextNormal.y)
                ).normalize();
                
                // Scale miter length to maintain consistent thickness
                float angle = (float)Math.acos(Math.max(-1.0f, Math.min(1.0f, 
                    prevNormal.x * currNormal.x + prevNormal.y * currNormal.y)));
                float scale = (float)(1.0f / Math.cos(angle / 2.0f));
                miter.mul(scale);
                
                float nextAngle = (float)Math.acos(Math.max(-1.0f, Math.min(1.0f,
                    currNormal.x * nextNormal.x + currNormal.y * nextNormal.y)));
                float nextScale = (float)(1.0f / Math.cos(nextAngle / 2.0f));
                nextMiter.mul(nextScale);
                
                // First triangle (start of edge)
                buffer.put(curr.x + miter.x * halfWidth);
                buffer.put(curr.y + miter.y * halfWidth);
                buffer.put(curr.x - miter.x * halfWidth);
                buffer.put(curr.y - miter.y * halfWidth);
                buffer.put(next.x + nextMiter.x * halfWidth);
                buffer.put(next.y + nextMiter.y * halfWidth);
                
                // Second triangle (end of edge)
                buffer.put(curr.x - miter.x * halfWidth);
                buffer.put(curr.y - miter.y * halfWidth);
                buffer.put(next.x - nextMiter.x * halfWidth);
                buffer.put(next.y - nextMiter.y * halfWidth);
                buffer.put(next.x + nextMiter.x * halfWidth);
                buffer.put(next.y + nextMiter.y * halfWidth);
            }
            return;
        }
        
        // Original code for other polygons
        for (int i = 0; i < vertices.length; i++) {
            Vector2f curr = vertices[i];
            Vector2f next = vertices[(i + 1) % vertices.length];
            Vector2f prev = vertices[(i + vertices.length - 1) % vertices.length];
            
            // Get miter normals for both vertices of this edge
            Vector2f startNormal = getMiterNormal(prev, curr, next);
            Vector2f endNormal = getMiterNormal(curr, next, vertices[(i + 2) % vertices.length]);
            
            // First triangle (start of edge)
            buffer.put(curr.x + startNormal.x * halfWidth);
            buffer.put(curr.y + startNormal.y * halfWidth);
            buffer.put(curr.x - startNormal.x * halfWidth);
            buffer.put(curr.y - startNormal.y * halfWidth);
            buffer.put(next.x + endNormal.x * halfWidth);
            buffer.put(next.y + endNormal.y * halfWidth);
            
            // Second triangle (end of edge)
            buffer.put(curr.x - startNormal.x * halfWidth);
            buffer.put(curr.y - startNormal.y * halfWidth);
            buffer.put(next.x - endNormal.x * halfWidth);
            buffer.put(next.y - endNormal.y * halfWidth);
            buffer.put(next.x + endNormal.x * halfWidth);
            buffer.put(next.y + endNormal.y * halfWidth);
        }
    }

    @Override
    public void drawCircle(Vector2f position, float radius, Vector4f color, float lineWidth, boolean filled) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f());
        shader.setVector4f("color", color);
        
        if (filled) {
            // Generate circle vertices for filled circle
            FloatBuffer vertices = BufferUtils.createFloatBuffer(CIRCLE_SEGMENTS * 2);
            for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
                float angle = (float) (2.0f * Math.PI * i / CIRCLE_SEGMENTS);
                vertices.put(position.x + radius * (float)Math.cos(angle));
                vertices.put(position.y + radius * (float)Math.sin(angle));
            }
            vertices.flip();
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glDrawArrays(GL_TRIANGLE_FAN, 0, CIRCLE_SEGMENTS);
        } else {
            // Convert circle to polygon points
            Vector2f[] circlePoints = new Vector2f[CIRCLE_SEGMENTS];
            for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
                float angle = (float) (2.0f * Math.PI * i / CIRCLE_SEGMENTS);
                float x = position.x + radius * (float)Math.cos(angle);
                float y = position.y + radius * (float)Math.sin(angle);
                circlePoints[i] = new Vector2f(x, y);
            }
            
            // Generate outline using unified method
            FloatBuffer vertices = BufferUtils.createFloatBuffer(CIRCLE_SEGMENTS * 6 * 2); // 6 vertices per edge (2 triangles)
            generateOutlineTriangles(vertices, circlePoints, lineWidth);
            vertices.flip();
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glDrawArrays(GL_TRIANGLES, 0, CIRCLE_SEGMENTS * 6);
        }
    }

    @Override
    public void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color, float lineWidth, boolean filled) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f());
        shader.setVector4f("color", color);
        
        float halfWidth = dimensions.x / 2.0f;
        float halfHeight = dimensions.y / 2.0f;
        float cos = (float)Math.cos(rotation);
        float sin = (float)Math.sin(rotation);
        
        // Generate rectangle corners
        Vector2f[] corners = new Vector2f[4];
        corners[0] = new Vector2f(position.x + (-halfWidth * cos - halfHeight * sin),
                                position.y + (-halfWidth * sin + halfHeight * cos)); // Top-left
        corners[1] = new Vector2f(position.x + (halfWidth * cos - halfHeight * sin),
                                position.y + (halfWidth * sin + halfHeight * cos));  // Top-right
        corners[2] = new Vector2f(position.x + (halfWidth * cos + halfHeight * sin),
                                position.y + (halfWidth * sin - halfHeight * cos));  // Bottom-right
        corners[3] = new Vector2f(position.x + (-halfWidth * cos + halfHeight * sin),
                                position.y + (-halfWidth * sin - halfHeight * cos)); // Bottom-left
        
        if (filled) {
            FloatBuffer vertices = BufferUtils.createFloatBuffer(8);
            for (Vector2f corner : corners) {
                vertices.put(corner.x);
                vertices.put(corner.y);
            }
            vertices.flip();
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        } else {
            // Generate outline using unified method
            FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 6 * 2); // 4 edges, 6 vertices per edge
            generateOutlineTriangles(vertices, corners, lineWidth);
            vertices.flip();
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
            glDrawArrays(GL_TRIANGLES, 0, 24); // 4 edges * 6 vertices
        }
    }

    @Override
    public void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color, float lineWidth, boolean filled) {
        shader.use();
        shader.setMatrix4f("projection", projection);
        shader.setMatrix4f("view", view);
        shader.setMatrix4f("model", new Matrix4f());
        shader.setVector4f("color", color);
        
        float cos = (float)Math.cos(rotation);
        float sin = (float)Math.sin(rotation);
        
        // Transform vertices to world space
        Vector2f[] worldVertices = new Vector2f[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            float x = vertices[i].x * cos - vertices[i].y * sin + position.x;
            float y = vertices[i].x * sin + vertices[i].y * cos + position.y;
            worldVertices[i] = new Vector2f(x, y);
        }
        
        if (filled) {
            FloatBuffer transformedVertices = BufferUtils.createFloatBuffer(vertices.length * 2);
            for (Vector2f vertex : worldVertices) {
                transformedVertices.put(vertex.x);
                transformedVertices.put(vertex.y);
            }
            transformedVertices.flip();
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, transformedVertices);
            glDrawArrays(GL_TRIANGLE_FAN, 0, vertices.length);
        } else {
            // Generate outline using unified method
            FloatBuffer transformedVertices = BufferUtils.createFloatBuffer(vertices.length * 6 * 2);
            generateOutlineTriangles(transformedVertices, worldVertices, lineWidth);
            transformedVertices.flip();
            
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, transformedVertices);
            glDrawArrays(GL_TRIANGLES, 0, vertices.length * 6);
        }
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