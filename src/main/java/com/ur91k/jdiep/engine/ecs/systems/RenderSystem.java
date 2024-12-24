package com.ur91k.jdiep.engine.ecs.systems;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import com.ur91k.jdiep.engine.graphics.ShaderProgram;
import com.ur91k.jdiep.engine.ecs.components.CameraComponent;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.World;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.core.Input;

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
    private final Matrix4f view;
    private static final float GRID_LINE_WIDTH = 1.0f;
    private final World world;
    private final Input input;
    
    // Grid rendering resources
    private final int gridVao;
    private final int gridVbo;
    
    public RenderSystem(World world, int windowWidth, int windowHeight, Input input) {
        this.world = world;
        this.input = input;
        this.view = new Matrix4f();
        
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
        
        projection = new Matrix4f().ortho(
            -windowWidth/2.0f, windowWidth/2.0f,
            -windowHeight/2.0f, windowHeight/2.0f,
            -1, 1
        );

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
    
    public void renderGrid(float worldWidth, float worldHeight, float gridSize, Vector4f gridColor) {
        logger.trace("Rendering grid: {}x{}, size={}, color={}", worldWidth, worldHeight, gridSize, gridColor);
        
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
        
        float halfWidth = worldWidth / 2;
        float halfHeight = worldHeight / 2;
        
        // Draw vertical lines
        for (float x = -halfWidth; x <= halfWidth; x += gridSize) {
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
        for (float y = -halfHeight; y <= halfHeight; y += gridSize) {
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
} 