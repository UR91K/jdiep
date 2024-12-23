package com.ur91k.jdiep.engine.ecs.scaling;

public class TankScaling {
    private static final float BASE_DIAMETER = 50.0f;  // Base diameter for mass = 1
    private static final float LOG_BASE = 2.0f;        // Log base for scaling
    
    public static float calculateDiameter(float mass) {
        return BASE_DIAMETER * (float)(Math.log(mass + 1) / Math.log(LOG_BASE));
    }
    
    public static float calculateScaleFactor(float mass) {
        return calculateDiameter(mass) / BASE_DIAMETER;
    }
} 