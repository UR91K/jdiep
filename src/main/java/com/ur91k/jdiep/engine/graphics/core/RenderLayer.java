package com.ur91k.jdiep.engine.graphics.core;

import com.ur91k.jdiep.engine.ecs.core.Component;

public class RenderLayer extends Component {
    public static final int BACKGROUND = 0;
    public static final int BODY = 1;
    public static final int TURRET = 2;
    public static final int FOREGROUND = 3;

    private int layer;

    public RenderLayer(int layer) {
        this.layer = layer;
    }

    public int getLayer() {
        return layer;
    }
} 