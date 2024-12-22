package com.ur91k.jdiep.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderSystem {
    private final ShaderProgram gameShader;
    private final Matrix4f projection;
    private static final float GRID_LINE_WIDTH = 0.5f;
    
    // Grid rendering resources
    private final int gridVao;
    private final int gridVbo;
    
    public RenderSystem(int windowWidth, int windowHeight) {
        // Initialize shader
        ClassLoader classLoader = getClass().getClassLoader();
        gameShader = new ShaderProgram(
            classLoader.getResourceAsStream("shaders/game_vertex.glsl"),
            classLoader.getResourceAsStream("shaders/game_fragment.glsl")
        );
        
        projection = new Matrix4f().ortho(
            0, windowWidth,
            0, windowHeight,
            -1, 1
        );
        
        // Initialize grid resources
        gridVao = glGenVertexArrays();
        gridVbo = glGenBuffers();
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        glBufferData(GL_ARRAY_BUFFER, 4 * Float.BYTES, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    }
    
    public void beginFrame() {
        glClear(GL_COLOR_BUFFER_BIT);
    }
    
    public void renderGrid(int windowWidth, int windowHeight, float gridSize, Vector4f gridColor) {
        gameShader.use();
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("model", new Matrix4f());
        gameShader.setVector4f("color", gridColor);
        
        glBindVertexArray(gridVao);
        glBindBuffer(GL_ARRAY_BUFFER, gridVbo);
        
        // Set thin line width for grid
        glLineWidth(GRID_LINE_WIDTH);
        
        FloatBuffer lineBuffer = BufferUtils.createFloatBuffer(4);
        
        // Draw vertical lines
        for (float x = 0; x <= windowWidth; x += gridSize) {
            lineBuffer.clear();
            lineBuffer.put(new float[] { x, 0, x, windowHeight });
            lineBuffer.flip();
            
            glBufferSubData(GL_ARRAY_BUFFER, 0, lineBuffer);
            glDrawArrays(GL_LINES, 0, 2);
        }
        
        // Draw horizontal lines
        for (float y = 0; y <= windowHeight; y += gridSize) {
            lineBuffer.clear();
            lineBuffer.put(new float[] { 0, y, windowWidth, y });
            lineBuffer.flip();
            
            glBufferSubData(GL_ARRAY_BUFFER, 0, lineBuffer);
            glDrawArrays(GL_LINES, 0, 2);
        }
    }
    
    public void setTransformAndColor(Matrix4f transform, Vector4f color) {
        gameShader.use();
        gameShader.setMatrix4f("projection", projection);
        gameShader.setMatrix4f("model", transform);
        gameShader.setVector4f("color", color);
    }
    
    public void cleanup() {
        gameShader.cleanup();
        glDeleteBuffers(gridVbo);
        glDeleteVertexArrays(gridVao);
    }
} 