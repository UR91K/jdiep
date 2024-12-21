package com.ur91k.jdiep.engine.ecs;

public abstract class System {
    protected World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public abstract void update();
} 