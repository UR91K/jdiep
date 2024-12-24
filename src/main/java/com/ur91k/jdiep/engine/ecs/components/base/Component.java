package com.ur91k.jdiep.engine.ecs.components.base;

import com.ur91k.jdiep.engine.ecs.entities.base.Entity;

public abstract class Component {
    protected Entity owner;
    
    public void setOwner(Entity owner) {
        this.owner = owner;
    }
    
    public Entity getOwner() {
        return owner;
    }
}