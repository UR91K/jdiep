package com.ur91k.jdiep.ecs.systems.render;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import com.ur91k.jdiep.game.config.GameConstants;
import com.ur91k.jdiep.graphics.core.ShaderProgram;
import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.World;

import java.nio.FloatBuffer;
import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderSystem {
    private static final Logger logger = Logger.getLogger(RenderSystem.class);
    private final ShaderProgram gameShader;
    private final Matrix4f projection;
    private final Matrix4f screenProjection;  // For UI/debug elements
    private final Matrix4f view;
    private static final float GRID_LINE_WIDTH = 1.0f;
    private final World world;
    private final Input input;
    private int windowWidth;
    private int windowHeight;
    private float aspectRatio;
    private static final float BASE_VIEW_HEIGHT = GameConstants.BASE_VIEW_HEIGHT;
    
    // Grid rendering resources
    private final int gridVao;
    private final int gridVbo;
    
    public RenderSystem(World world, int windowWidth, int windowHeight, Input input) {
        this.world = world;
        this.input = input;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.aspectRatio = (float)windowWidth / windowHeight;
        this.view = new Matrix4f();
        
        // World space projection (center origin)
        float viewWidth = BASE_VIEW_HEIGHT * aspectRatio;
        projection = new Matrix4f().ortho(
            -viewWidth/2.0f, viewWidth/2.0f,
            -BASE_VIEW_HEIGHT/2.0f, BASE_VIEW_HEIGHT/2.0f,
            -1, 1
        );
        
        // Screen space projection (top-left origin)
        screenProjection = new Matrix4f().ortho(
            0, windowWidth,
            windowHeight, 0,  // Flip Y coordinates
            -1, 1
        );

        // Initialize shader
        ClassLoader classLoader = getClass().getClassLoader();
        gameShader = new ShaderProgram(
            classLoader.getResourceAsStream("shaders/game_vertex.glsl"),
            classLoader.getResourceAsStream("shaders/game_fragment.glsl")
        );

        // Verify shader uniforms
        gameShader.use();
        int projLoc = glGetUniformLocation(gameShader.getProgramId(), "projection");
        int viewLoc = glGetUniformLocation(gameShader.getProgramId(), "view");
        int modelLoc = glGetUniformLocation(gameShader.getProgramId(), "model");
        int colorLoc = glGetUniformLocation(gameShader.getProgramId(), "color");
        
        logger.debug("Shader uniform locations - projection: {}, view: {}, model: {}, color: {}", 
            projLoc, viewLoc, modelLoc, colorLoc);
        
        // Create grid VAO/VBO
        gridVao = glGenVertexArrays();
        gridVbo = glGenBuffers();
        
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        glBufferData(GL_ARRAY_BUFFER, 8 * Float.BYTES, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        // Verify VAO/VBO setup
        if (glGetError() != GL_NO_ERROR) {
            logger.error("Error during VAO/VBO setup");
        }
    }

    public void beginFrame() {
        glClear(GL_COLOR_BUFFER_BIT);
        updateViewMatrix();
    }

    private void updateViewMatrix() {
        Collection<Entity> cameras = world.getEntitiesWith(CameraComponent.class);
        if (cameras.isEmpty()) {
            view.identity();
            logger.debug("No camera found, using identity view matrix");
            return;
        }

        Entity cameraEntity = cameras.iterator().next();
        CameraComponent camera = cameraEntity.getComponent(CameraComponent.class);
        
        view.identity()
            .scale(camera.getZoom())  // Apply zoom
            .translate(-camera.getPosition().x, -camera.getPosition().y, 0);  // Center on camera

        logger.trace("Updated view matrix - Camera position: {}, Zoom: {}", 
            camera.getPosition(), camera.getZoom());
        logger.trace("View matrix: {}", view);
        logger.trace("Projection matrix: {}", projection);

        // Update Input system with current matrices
        input.setViewMatrix(view);
        input.setProjectionMatrix(projection);
    }
    
    public void renderGrid(float numLinesX, float numLinesY, float gridSize, Vector4f gridColor) {
        logger.trace("Rendering grid: {}x{} lines, size={}, color={}", numLinesX, numLinesY, gridSize, gridColor);
        
        // Verify shader program is valid
        gameShader.use();
        glValidateProgram(gameShader.getProgramId());
        if (glGetProgrami(gameShader.getProgramId(), GL_VALIDATE_STATUS) != GL_TRUE) {
            logger.error("Shader program validation failed: {}", glGetProgramInfoLog(gameShader.getProgramId()));
            return;
        }

        // Set uniforms and verify
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("view", view);
        gameShader.setMatrix4f("model", new Matrix4f());
        gameShader.setVector4f("color", gridColor);

        int error = glGetError();
        if (error != GL_NO_ERROR) {
            logger.error("Error setting uniforms: {}", error);
            return;
        }

        // Bind buffers
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);

        error = glGetError();
        if (error != GL_NO_ERROR) {
            logger.error("Error binding buffers: {}", error);
            return;
        }

        glLineWidth(GRID_LINE_WIDTH);
        
        // Enable blending for lines
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        FloatBuffer lineBuffer = BufferUtils.createFloatBuffer(4);
        
        // Calculate total world dimensions based on number of lines and grid size
        float worldWidth = (numLinesX - 1) * gridSize;
        float worldHeight = (numLinesY - 1) * gridSize;
        float halfWidth = worldWidth / 2;
        float halfHeight = worldHeight / 2;
        
        // Draw vertical lines
        for (int i = 0; i < numLinesX; i++) {
            float x = -halfWidth + (i * gridSize);
            lineBuffer.clear();
            float[] vertices = new float[] { x, -halfHeight, x, halfHeight };
            lineBuffer.put(vertices);
            lineBuffer.flip();
            
            glBufferSubData(GL_ARRAY_BUFFER, 0, lineBuffer);
            glDrawArrays(GL_LINES, 0, 2);
            
            error = glGetError();
            if (error != GL_NO_ERROR) {
                logger.error("Error drawing vertical line at x={}: {}", x, error);
                return;
            }
        }
        
        // Draw horizontal lines
        for (int i = 0; i < numLinesY; i++) {
            float y = -halfHeight + (i * gridSize);
            lineBuffer.clear();
            float[] vertices = new float[] { -halfWidth, y, halfWidth, y };
            lineBuffer.put(vertices);
            lineBuffer.flip();
            
            glBufferSubData(GL_ARRAY_BUFFER, 0, lineBuffer);
            glDrawArrays(GL_LINES, 0, 2);
            
            error = glGetError();
            if (error != GL_NO_ERROR) {
                logger.error("Error drawing horizontal line at y={}: {}", y, error);
                return;
            }
        }
        
        // Cleanup state
        glDisable(GL_BLEND);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    public void setTransformAndColor(Matrix4f model, Vector4f color) {
        gameShader.use();
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("view", view);
        gameShader.setMatrix4f("model", model);
        gameShader.setVector4f("color", color);
    }
    
    public void cleanup() {
        gameShader.cleanup();
        glDeleteBuffers(gridVbo);
        glDeleteVertexArrays(gridVao);
    }

    public void drawLine(Vector2f start, Vector2f end, Vector4f color, float thickness) {
        gameShader.use();
        glLineWidth(thickness);
        
        // Enable blending for lines
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Set uniforms
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("view", view);
        gameShader.setMatrix4f("model", new Matrix4f());
        gameShader.setVector4f("color", color);
        
        // Draw line
        FloatBuffer lineBuffer = BufferUtils.createFloatBuffer(4);
        float[] vertices = new float[] { 
            start.x, start.y,
            end.x, end.y 
        };
        lineBuffer.put(vertices).flip();
        
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, lineBuffer);
        glDrawArrays(GL_LINES, 0, 2);
        
        // Cleanup state
        glDisable(GL_BLEND);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    public void drawScreenLine(Vector2f start, Vector2f end, Vector4f color, float thickness) {
        Matrix4f oldView = new Matrix4f(view);
        Matrix4f oldProj = new Matrix4f(projection);
        
        // Switch to screen space
        view.identity();
        gameShader.setMatrix4f("projection", screenProjection);
        
        // Disable line smoothing for crisp debug shapes
        glDisable(GL_LINE_SMOOTH);
        
        drawLine(start, end, color, thickness);
        
        // Restore previous state
        view.set(oldView);
        gameShader.setMatrix4f("projection", oldProj);
        glEnable(GL_LINE_SMOOTH);
    }
    
    public void drawCircle(Vector2f center, float radius, Vector4f color, float thickness) {
        gameShader.use();
        glLineWidth(thickness);
        
        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Set uniforms
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("view", view);
        gameShader.setMatrix4f("model", new Matrix4f().translate(center.x, center.y, 0));
        gameShader.setVector4f("color", color);
        
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
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        glBufferData(GL_ARRAY_BUFFER, circleBuffer, GL_DYNAMIC_DRAW);
        glDrawArrays(GL_LINE_LOOP, 0, segments);
        
        // Cleanup state
        glDisable(GL_BLEND);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    public void drawScreenCircle(Vector2f center, float radius, Vector4f color, float thickness) {
        Matrix4f oldView = new Matrix4f(view);
        Matrix4f oldProj = new Matrix4f(projection);
        
        // Switch to screen space
        view.identity();
        gameShader.setMatrix4f("projection", screenProjection);
        
        // Disable line smoothing for crisp debug shapes
        glDisable(GL_LINE_SMOOTH);
        
        drawCircle(center, radius, color, thickness);
        
        // Restore previous state
        view.set(oldView);
        gameShader.setMatrix4f("projection", oldProj);
        glEnable(GL_LINE_SMOOTH);
    }

    public void drawRect(Vector2f position, float width, float height, Vector4f color, float thickness) {
        gameShader.use();
        glLineWidth(thickness);
        
        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        // Set uniforms
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("view", view);
        gameShader.setMatrix4f("model", new Matrix4f());
        gameShader.setVector4f("color", color);
        
        // Draw rectangle outline
        FloatBuffer vertices = BufferUtils.createFloatBuffer(10);  // 5 vertices * 2 coordinates
        float[] rectVertices = new float[] {
            position.x, position.y,                    // Bottom left
            position.x + width, position.y,            // Bottom right
            position.x + width, position.y + height,   // Top right
            position.x, position.y + height,           // Top left
            position.x, position.y                     // Back to start
        };
        vertices.put(rectVertices).flip();
        
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        glDrawArrays(GL_LINE_STRIP, 0, 5);
        
        // Cleanup state
        glDisable(GL_BLEND);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
    
    public void drawScreenRect(Vector2f position, float width, float height, Vector4f color, float thickness) {
        Matrix4f oldView = new Matrix4f(view);
        Matrix4f oldProj = new Matrix4f(projection);
        
        // Switch to screen space
        view.identity();
        gameShader.setMatrix4f("projection", screenProjection);
        
        // Disable line smoothing for crisp debug shapes
        glDisable(GL_LINE_SMOOTH);
        
        drawRect(position, width, height, color, thickness);
        
        // Restore previous state
        view.set(oldView);
        gameShader.setMatrix4f("projection", oldProj);
        glEnable(GL_LINE_SMOOTH);
    }

    public void handleResize(int newWidth, int newHeight) {
        this.windowWidth = newWidth;
        this.windowHeight = newHeight;
        this.aspectRatio = (float)newWidth / newHeight;
        
        // Update world space projection (center origin)
        float viewWidth = BASE_VIEW_HEIGHT * aspectRatio;
        projection.identity().ortho(
            -viewWidth/2.0f, viewWidth/2.0f,
            -BASE_VIEW_HEIGHT/2.0f, BASE_VIEW_HEIGHT/2.0f,
            -1, 1
        );
        
        // Update screen space projection (top-left origin)
        screenProjection.identity().ortho(
            0, windowWidth,
            windowHeight, 0,  // Flip Y coordinates
            -1, 1
        );
        
        // Update viewport
        glViewport(0, 0, windowWidth, windowHeight);
    }
} 