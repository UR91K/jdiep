package com.ur91k.jdiep.game.config;

/**
 * Defines the base units and scaling factors for the game.
 * The game uses a meter-based system where 1 meter = 30 pixels at 1.0x zoom.
 */
public class GameUnits {
    // Base conversion rate: 1 meter = 30 pixels at 1.0x zoom
    public static final float PIXELS_PER_METER = 20.0f;
    
    // World dimensions in meters
    public static final float WORLD_WIDTH = 1024.0f;
    public static final float WORLD_HEIGHT = 1024.0f;
    
    // Line thicknesses in meters
    public static final float DEFAULT_LINE_THICKNESS = 0.18f;
    
    // Grid
    public static final float GRID_CELL_SIZE = 1.0f;  // 1 meter per grid cell
    
    // Tank dimensions (in meters)
    public static class Tank {
        public static final float BODY_RADIUS = 2.3f;
        public static final float TURRET_RADIUS_RATIO = 1.00f;
        public static final float TURRET_LENGTH_RATIO = 2f;
        
        public static float getTurretRadius() {
            return BODY_RADIUS * TURRET_RADIUS_RATIO;
        }
        
        public static float getTurretLength() {
            return BODY_RADIUS * TURRET_LENGTH_RATIO;
        }
    }
    
    // Food dimensions (in meters)
    public static class Food {
        public static final float TINY_SIDE_LENGTH = 1.56f;
        public static final float SMALL_SIDE_LENGTH = 1.87f;
        public static final float MEDIUM_SIDE_LENGTH = 1.72f;
        public static final float LARGE_SIDE_LENGTH = 4.80f;
    }
    
    // Drone dimensions (in meters)
    public static class Drone {
        public static final float SIDE_LENGTH = 1.0f;
    }
    
    // Conversion helpers
    public static float metersToPixels(float meters) {
        return meters * PIXELS_PER_METER;
    }
    
    public static float pixelsToMeters(float pixels) {
        return pixels / PIXELS_PER_METER;
    }
    
    public static float scaleToZoom(float value, float zoom) {
        return value * zoom;
    }
} 