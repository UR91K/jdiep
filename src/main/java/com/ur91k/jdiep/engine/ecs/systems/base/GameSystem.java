package com.ur91k.jdiep.engine.ecs.systems.base;

import com.ur91k.jdiep.engine.ecs.World;

public abstract class GameSystem extends System {
    protected World world;
    protected boolean isClient;  // For future network split
    
    public GameSystem(World world, boolean isClient) {
        this.world = world;
        this.isClient = isClient;
    }
    
    public abstract void update();
} 