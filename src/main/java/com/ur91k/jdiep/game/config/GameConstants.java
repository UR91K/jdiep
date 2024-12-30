package com.ur91k.jdiep.game.config;

public final class GameConstants {
    private GameConstants() {} // Prevent instantiation
    
    // Base game units (in world space)
    public static final float BASE_VIEW_HEIGHT = 1000.0f;  // Height of the game view in world units
    public static final float DEFAULT_TANK_SIZE = 30.0f;   // Base tank size in world units
    public static final float GRID_SIZE = 40.0f;           // Size of grid cells in world units
    
    // UI constants (in screen space)
    public static final float UI_PADDING = 10.0f;          // Padding between UI elements in pixels
    public static final float UI_SCALE = 1.0f;             // Base UI scale factor
    
    // Scaling constants
    public static final float MIN_ZOOM = 0.1f;             // Minimum zoom level
    public static final float MAX_ZOOM = 5.0f;             // Maximum zoom level
    public static final float DEFAULT_ZOOM = 1.0f;         // Default zoom level
    public static final float ZOOM_SPEED = 0.1f;           // Zoom speed multiplier
    
    // World bounds
    public static final float WORLD_BOUNDS = 5000.0f;      // World boundary size (half-width/height)
} 