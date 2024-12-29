package com.ur91k.jdiep.graphics.config;

import org.joml.Vector4f;

public class RenderingConstants {
    // Background colors
    public static final Vector4f BACKGROUND_COLOR = new Vector4f(0.8f, 0.8f, 0.8f, 1.0f);  // Light gray
    public static final Vector4f GRID_COLOR = new Vector4f(0.75f, 0.75f, 0.75f, 1.0f);     // Slightly darker gray
    public static final float GRID_SIZE = 32.0f;
    
    // Tank colors
    public static final Vector4f RED_FILL_COLOR = new Vector4f(0.8f, 0.2f, 0.2f, 1.0f);
    public static final Vector4f RED_OUTLINE_COLOR = new Vector4f(0.6f, 0.1f, 0.1f, 1.0f);
    
    // Turret colors
    public static final Vector4f TURRET_FILL_COLOR = new Vector4f(0.6f, 0.6f, 0.6f, 1.0f);
    public static final Vector4f TURRET_OUTLINE_COLOR = new Vector4f(0.4f, 0.4f, 0.4f, 1.0f);
    
    // Food colors
    public static final Vector4f T_FOOD_FILL = new Vector4f(1.0f, 0.8f, 0.2f, 1.0f);      // Yellow
    public static final Vector4f T_FOOD_OUTLINE = new Vector4f(0.8f, 0.6f, 0.1f, 1.0f);
    public static final Vector4f S_FOOD_FILL = new Vector4f(0.9f, 0.3f, 0.3f, 0.8f);      // Faded red
    public static final Vector4f S_FOOD_OUTLINE = new Vector4f(0.7f, 0.2f, 0.2f, 0.8f);
    public static final Vector4f M_FOOD_FILL = new Vector4f(0.2f, 0.4f, 1.0f, 1.0f);      // Blue
    public static final Vector4f M_FOOD_OUTLINE = new Vector4f(0.1f, 0.3f, 0.8f, 1.0f);
    
    // Drone colors
    public static final Vector4f DRONE_FILL_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);  // Gray
    public static final Vector4f DRONE_OUTLINE_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
    
    // Bullet colors
    public static final Vector4f BULLET_FILL_COLOR = new Vector4f(0.8f, 0.2f, 0.2f, 1.0f);  // Red
    public static final Vector4f BULLET_OUTLINE_COLOR = new Vector4f(0.6f, 0.1f, 0.1f, 1.0f);
    
    // Layer constants
    public static final float LAYER_SPACING = 0.01f;  // Space between layers
    
    // Default sizes
    public static final float DEFAULT_OUTLINE_WIDTH = 2.0f;
    public static final float DEFAULT_TANK_RADIUS = 30.0f;
    public static final float DEFAULT_BULLET_RADIUS = 5.0f;
} 