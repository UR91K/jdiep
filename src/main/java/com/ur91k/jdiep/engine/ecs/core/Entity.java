package com.ur91k.jdiep.engine.ecs.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ur91k.jdiep.ecs.components.transform.TransformComponent;

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
        if (component instanceof TransformComponent) {
            ((TransformComponent) component).setEntity(this);
        }
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

    public Collection<Component> getComponents() {
        return components.values();
    }
}