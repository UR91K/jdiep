package com.ur91k.jdiep.engine.ecs;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private boolean active = true;
    
    public <T extends Component> T addComponent(T component) {
        component.setOwner(this);
        components.put(component.getClass(), component);
        return component;
    }
    
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }
    
    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
}