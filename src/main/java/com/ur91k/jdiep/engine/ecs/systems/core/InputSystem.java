package com.ur91k.jdiep.engine.ecs.systems.core;

import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.engine.ecs.core.World;

public abstract class InputSystem extends GameSystem {
    protected Input input;
    
    public InputSystem(World world, Input input) {
        super(world, true);  // Input systems are client-only
        this.input = input;
    }
} 