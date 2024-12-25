package com.ur91k.jdiep.ecs.core;

public abstract class Component {
    protected Entity owner;
    
    public void setOwner(Entity owner) {
        this.owner = owner;
    }
    
    public Entity getOwner() {
        return owner;
    }
}