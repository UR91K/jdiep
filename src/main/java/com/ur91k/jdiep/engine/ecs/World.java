package com.ur91k.jdiep.engine.ecs;

import java.util.*;

public class World {
    private final Map<Long, Entity> entities;
    private final List<System> systems;
    private long nextEntityId;

    public World() {
        this.entities = new HashMap<>();
        this.systems = new ArrayList<>();
        this.nextEntityId = 1;
    }

    public Entity createEntity() {
        Entity entity = new Entity(nextEntityId++);
        entities.put(entity.getId(), entity);
        return entity;
    }

    public void destroyEntity(Entity entity) {
        entities.remove(entity.getId());
    }

    public void addSystem(System system) {
        systems.add(system);
        system.setWorld(this);
    }

    public Collection<Entity> getEntities() {
        return entities.values();
    }

    // Get entities that have all the specified components
    @SafeVarargs
    public final List<Entity> getEntitiesWith(Class<? extends Component>... componentTypes) {
        return entities.values().stream()
            .filter(entity -> Arrays.stream(componentTypes)
                .allMatch(entity::hasComponent))
            .toList();
    }

    public Entity getEntity(long id) {
        return entities.get(id);
    }

    public void update() {
        for (System system : systems) {
            system.update();
        }
    }
} 