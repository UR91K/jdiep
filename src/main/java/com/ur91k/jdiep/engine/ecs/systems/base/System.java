package com.ur91k.jdiep.engine.ecs.systems.base;

import com.ur91k.jdiep.engine.ecs.World;

public abstract class System {
    protected World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public abstract void update();
} 