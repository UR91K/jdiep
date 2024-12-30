package com.ur91k.jdiep.game.config;

public final class GameConstants {
    private GameConstants() {} // Prevent instantiation
    
    // Base game units (in world space)
    public static final float BASE_VIEW_HEIGHT = GameUnits.pixelsToMeters(720.0f);  // Height of the game view in meters
    public static final float DEFAULT_TANK_SIZE = GameUnits.Tank.BODY_RADIUS * 2;   // Base tank size in meters
    public static final float GRID_SIZE = GameUnits.GRID_CELL_SIZE;                 // Size of grid cells in meters
    
    // UI constants (in meters for consistent scaling)
    public static final float UI_PADDING = GameUnits.pixelsToMeters(10.0f);         // Padding between UI elements in meters
    public static final float UI_SCALE = 1.0f;                                      // Base UI scale factor
    
    // Scaling constants
    public static final float MIN_ZOOM = 0.1f;             // Minimum zoom level
    public static final float MAX_ZOOM = 5.0f;             // Maximum zoom level
    public static final float DEFAULT_ZOOM = 1.0f;         // Default zoom level
    public static final float ZOOM_SPEED = 0.1f;           // Zoom speed multiplier
    
    // World bounds
    public static final float WORLD_BOUNDS = 1024.0f;      // World boundary size (half-width/height)

    // Food sizes
    public static final float TINY_FOOD_SIZE = 1.56f;     // Square side length
    public static final float SMALL_FOOD_SIZE = 1.87f;    // Triangle side length
    public static final float MEDIUM_FOOD_SIZE = 1.72f;   // Pentagon side length
    public static final float LARGE_FOOD_SIZE = 4.80f;    // Large pentagon side length
} 