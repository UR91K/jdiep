package com.ur91k.jdiep.graphics.core;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public interface Renderer {
    // Old methods for backward compatibility
    void drawCircle(Vector2f position, float radius, Vector4f color);
    void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color);
    void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color);
    
    // New methods with fill and line width support
    void drawCircle(Vector2f position, float radius, Vector4f color, float lineWidth, boolean filled);
    void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color, float lineWidth, boolean filled);
    void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color, float lineWidth, boolean filled);
    
    void setView(Matrix4f view);
    void drawGrid();
} 