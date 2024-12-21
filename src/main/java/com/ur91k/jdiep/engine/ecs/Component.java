package com.ur91k.jdiep.engine.ecs;
public abstract class Component {
    protected Entity owner;
    
    public void setOwner(Entity owner) {
        this.owner = owner;
    }
    
    public Entity getOwner() {
        return owner;
    }
}