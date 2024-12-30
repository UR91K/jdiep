package com.ur91k.jdiep.graphics.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import com.ur91k.jdiep.game.config.GameUnits;

public interface Renderer {
    // Old methods for backward compatibility
    default void drawCircle(Vector2f position, float radius, Vector4f color) {
        drawCircle(position, radius, color, GameUnits.DEFAULT_LINE_THICKNESS, true);
    }
    
    default void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color) {
        drawRectangle(position, dimensions, rotation, color, GameUnits.DEFAULT_LINE_THICKNESS, true);
    }
    
    default void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color) {
        drawPolygon(position, vertices, rotation, color, GameUnits.DEFAULT_LINE_THICKNESS, true);
    }
    
    // New methods with fill and line width support
    void drawCircle(Vector2f position, float radius, Vector4f color, float lineWidth, boolean filled);
    void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color, float lineWidth, boolean filled);
    void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color, float lineWidth, boolean filled);
    
    void setView(Matrix4f view);
    void drawGrid();
    void cleanup();
    
    // Handle window resize
    void handleResize(int newWidth, int newHeight);
} 