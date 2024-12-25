package com.ur91k.jdiep.ecs.core;

public abstract class System {
    protected World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public abstract void update();
} 