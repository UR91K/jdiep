package com.ur91k.jdiep.game.config;

public class TankScaling {
    private static final float BASE_RADIUS = 1.13f;  // Starting radius in meters
    private static final float LOG_BASE = (float)Math.E;  // Natural log for simpler math
    private static final float SCALE_FACTOR = 0.1f;  // Controls growth rate
    private static final float COMPRESSION = 1000.0f;   // Controls how quickly growth slows down
    
    public static float calculateRadius(float mass) {
        // At mass 0, radius should be BASE_RADIUS (1.13)
        // For mass > 0, scale up with diminishing returns
        return BASE_RADIUS * (1.0f + SCALE_FACTOR * (float)Math.log1p(mass / COMPRESSION));
    }
    
    public static float calculateDiameter(float mass) {
        return calculateRadius(mass) * 2.0f;
    }
    
    public static float calculateScaleFactor(float mass) {
        return calculateRadius(mass) / BASE_RADIUS;
    }
} 