package com.ur91k.jdiep.graphics.core;

import com.badlogic.ashley.core.Component;

public class RenderLayer implements Component {
    public static final int BACKGROUND = 0;
    public static final int GAME_OBJECTS = 100;
    public static final int EFFECTS = 200;
    public static final int UI = 300;
    public static final int DEBUG = 400;

    private int layer;

    public RenderLayer() {
        this.layer = GAME_OBJECTS;  // Default to game objects layer
    }

    public RenderLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
} 