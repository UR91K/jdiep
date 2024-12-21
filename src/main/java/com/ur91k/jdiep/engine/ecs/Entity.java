package com.ur91k.jdiep.engine.ecs;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    private final long id;
    private final Map<Class<? extends Component>, Component> components;

    public Entity() {
        this(0); // For legacy support
    }

    public Entity(long id) {
        this.id = id;
        this.components = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        components.remove(componentClass);
    }
}