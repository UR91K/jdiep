package com.ur91k.jdiep.ecs.components.physics;

/**
 * Defines physical properties for different game entities.
 * These values affect how entities move and collide.
 */
public final class PhysicsProperties {
    private PhysicsProperties() {} // Prevent instantiation
    
    // Tank Properties
    public static final float TANK_DENSITY = 1.0f;
    public static final float TANK_FRICTION = 0.1f;
    public static final float TANK_RESTITUTION = 0.4f;  // Moderate bounce
    public static final float TANK_LINEAR_DAMPING = 0.5f;  // Slow down movement
    public static final float TANK_ANGULAR_DAMPING = 0.8f;  // Slow down rotation
    
    // Turret Properties (Kinematic - moved by code, not physics)
    public static final float TURRET_DENSITY = 0.0f;
    public static final float TURRET_FRICTION = 0.0f;
    public static final float TURRET_RESTITUTION = 0.0f;
    
    // Common Properties
    public static final float PHYSICS_SCALE = 100.0f;  // Pixels per meter
    public static final float MIN_LINEAR_VELOCITY = 0.01f;  // Threshold to stop movement
    public static final float MIN_ANGULAR_VELOCITY = 0.01f;  // Threshold to stop rotation
    
    // World Properties
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;
    public static final float FIXED_TIME_STEP = 1.0f / 60.0f;
    
    /**
     * Convert game units to physics units (meters)
     */
    public static float toPhysicsUnits(float gameUnits) {
        return gameUnits / PHYSICS_SCALE;
    }
    
    /**
     * Convert physics units (meters) to game units
     */
    public static float toGameUnits(float physicsUnits) {
        return physicsUnits * PHYSICS_SCALE;
    }
} 