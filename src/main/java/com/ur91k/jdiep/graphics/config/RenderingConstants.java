package com.ur91k.jdiep.graphics.config;

import org.joml.Vector4f;
import static com.ur91k.jdiep.core.utils.ColorUtils.rgb;

public class RenderingConstants {
    // Background colors
    public static final Vector4f BACKGROUND_COLOR = rgb(0xcccccc);
    public static final Vector4f GRID_COLOR = rgb(0xc4c4c4);
    public static final float GRID_SIZE = 24.0f;
    
    // Tank colors
    public static final Vector4f RED_FILL_COLOR = rgb(0xfa4c57);
    public static final Vector4f RED_OUTLINE_COLOR = rgb(0xbb3941);
    
    // Turret colors
    public static final Vector4f TURRET_FILL_COLOR = rgb(0x999999);
    public static final Vector4f TURRET_OUTLINE_COLOR = rgb(0x727272);
    
    // Food colors
    public static final Vector4f T_FOOD_FILL = rgb(0xffe777);
    public static final Vector4f T_FOOD_OUTLINE = rgb(0xc0ae58);
    
    public static final Vector4f S_FOOD_FILL = rgb(0xff7579);
    public static final Vector4f S_FOOD_OUTLINE = rgb(0xc4575a);
    
    public static final Vector4f M_FOOD_FILL = rgb(0x768dfc);
    public static final Vector4f M_FOOD_OUTLINE = rgb(0x5869bd);

    public static final Vector4f L_FOOD_FILL = M_FOOD_FILL;
    public static final Vector4f L_FOOD_OUTLINE = M_FOOD_OUTLINE;
    
    // Drone colors
    public static final Vector4f DRONE_FILL_COLOR = new Vector4f(0.5f, 0.5f, 0.5f, 1.0f);  // Gray
    public static final Vector4f DRONE_OUTLINE_COLOR = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
    
    // Bullet colors
    public static final Vector4f BULLET_FILL_COLOR = new Vector4f(0.8f, 0.2f, 0.2f, 1.0f);  // Red
    public static final Vector4f BULLET_OUTLINE_COLOR = new Vector4f(0.6f, 0.1f, 0.1f, 1.0f);
    
    // Layer constants
    public static final float LAYER_SPACING = 0.01f;  // Space between layers
    
    // Default sizes
    public static final float DEFAULT_OUTLINE_WIDTH = 4.0f;
} 