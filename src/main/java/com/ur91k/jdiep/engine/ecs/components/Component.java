package com.ur91k.jdiep.engine.ecs.components;

import com.ur91k.jdiep.engine.ecs.entities.Entity;

public abstract class Component {
    protected Entity owner;
    
    public void setOwner(Entity owner) {
        this.owner = owner;
    }
    
    public Entity getOwner() {
        return owner;
    }
}