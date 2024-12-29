package com.ur91k.jdiep.graphics.core;

import org.joml.Vector2f;
import org.joml.Vector4f;

public interface Renderer {
    void drawCircle(Vector2f position, float radius, Vector4f color);
    void drawRectangle(Vector2f position, Vector2f dimensions, float rotation, Vector4f color);
    void drawPolygon(Vector2f position, Vector2f[] vertices, float rotation, Vector4f color);
} 