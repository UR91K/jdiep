package com.ur91k.jdiep.graphics.config;

import org.joml.Vector4f;

public final class RenderingConstants {
    private RenderingConstants() {} // Prevent instantiation
    
    //BACKGROUND COLORS
    public static final Vector4f BACKGROUND_COLOR = rgb(0xcccccc);
    public static final Vector4f GRID_COLOR = rgb(0xc1c1c1);
    public static final float GRID_SIZE = 32.0f;
    
    //FOOD COLORS
    public static final Vector4f T_FOOD_FILL = rgb(0xffe777);
    public static final Vector4f T_FOOD_OUTLINE = rgb(0xc0ae58);
    
    public static final Vector4f S_FOOD_FILL = rgb(0xff7579);
    public static final Vector4f S_FOOD_OUTLINE = rgb(0xc4575a);
    
    public static final Vector4f M_FOOD_FILL = rgb(0x768dfc);
    public static final Vector4f M_FOOD_OUTLINE = rgb(0x5869bd);

    //TEAM COLORS
    public static final Vector4f PURPLE_FILL = rgb(0xbf7ff5);
    public static final Vector4f PURPLE_OUTLINE = rgb(0x8f5fb7);

    public static final Vector4f GREEN_FILL = rgb(0x00e16e);
    public static final Vector4f GREEN_OUTLINE_COLOR = rgb(0x00a852);

    public static final Vector4f RED_FILL_COLOR = rgb(0xfa4c57);
    public static final Vector4f RED_OUTLINE_COLOR = rgb(0xbb3941);

    public static final Vector4f BLUE_FILL_COLOR = rgb(0x00b3de);
    public static final Vector4f BLUE_OUTLINE_COLOR = rgb(0x0085a6);

    //TURRET COLORS
    public static final Vector4f TURRET_FILL_COLOR = rgb(0x999999);
    public static final Vector4f TURRET_OUTLINE_COLOR = rgb(0x727272);

    //HELPER FUNCTIONS
    private static Vector4f rgb(int r, int g, int b) {
        return new Vector4f(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
    }
    
    @SuppressWarnings("unused")
    public
    static Vector4f rgb(int hex) {
        return rgb((hex >> 16) & 0xFF, (hex >> 8) & 0xFF, hex & 0xFF);
    }
} 