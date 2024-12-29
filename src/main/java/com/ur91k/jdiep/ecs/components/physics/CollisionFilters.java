package com.ur91k.jdiep.ecs.components.physics;

/**
 * Defines collision categories and masks for the physics system.
 * Each category is a bit flag, and masks determine what can collide with what.
 */
public final class CollisionFilters {
    private CollisionFilters() {} // Prevent instantiation
    
    // Categories (each must be a power of 2)
    public static final short CATEGORY_TANK = 0x0001;
    public static final short CATEGORY_TURRET = 0x0002;
    public static final short CATEGORY_FOOD = 0x0004;
    public static final short CATEGORY_DRONE = 0x0008;
    public static final short CATEGORY_BULLET = 0x0010;
    public static final short CATEGORY_WALL = 0x0020;
    
    // Collision Masks (what each category can collide with)
    public static final short MASK_TANK = 
        CATEGORY_TANK | CATEGORY_FOOD | CATEGORY_DRONE | CATEGORY_BULLET | CATEGORY_WALL;
    
    public static final short MASK_TURRET = 0; // Don't collide with anything (kinematic)
    
    public static final short MASK_FOOD = 
        CATEGORY_TANK | CATEGORY_WALL;  // Only collide with tanks and walls
    
    public static final short MASK_DRONE = 
        CATEGORY_TANK | CATEGORY_BULLET | CATEGORY_WALL;  // No food or other drone collisions
    
    public static final short MASK_BULLET = 
        CATEGORY_TANK | CATEGORY_DRONE | CATEGORY_WALL;
    
    public static final short MASK_WALL = -1;  // Collide with everything
} 