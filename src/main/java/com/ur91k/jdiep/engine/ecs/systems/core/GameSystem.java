package com.ur91k.jdiep.engine.ecs.systems.core;

import com.ur91k.jdiep.ecs.core.System;
import com.ur91k.jdiep.ecs.core.World;

public abstract class GameSystem extends System {
    protected World world;
    protected boolean isClient;  // For future network split
    
    public GameSystem(World world, boolean isClient) {
        this.world = world;
        this.isClient = isClient;
    }
    
    public abstract void update();
} 