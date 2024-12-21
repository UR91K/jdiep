package com.ur91k.jdiep.engine.graphics;

import org.joml.Vector4f;

public final class RenderingConstants {
    private RenderingConstants() {} // Prevent instantiation
    
    public static final Vector4f BACKGROUND_COLOR = rgb(0xcc, 0xcc, 0xcc);
    public static final Vector4f GRID_COLOR = rgb(0xc1, 0xc1, 0xc1);
    public static final float GRID_SIZE = 32.0f;
    
    private static Vector4f rgb(int r, int g, int b) {
        return new Vector4f(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
    }
    
    private static Vector4f rgb(int hex) {
        return rgb((hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF);
    }
} 