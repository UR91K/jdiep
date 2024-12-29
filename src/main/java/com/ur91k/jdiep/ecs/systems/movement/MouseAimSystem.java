package com.ur91k.jdiep.ecs.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import org.joml.Vector2f;

public class MouseAimSystem extends IteratingSystem {
    private final Input input;
    private final ComponentMapper<TransformComponent> transformMapper;

    public MouseAimSystem(Input input) {
        super(Family.all(TransformComponent.class, PlayerControlledComponent.class).get());
        this.input = input;
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        
        // Get world space mouse position
        Vector2f mousePos = input.getWorldMousePosition();
        Vector2f entityPos = transform.getPosition();
        
        // Calculate direction to mouse
        Vector2f direction = new Vector2f(mousePos).sub(entityPos);
        
        // Calculate rotation angle (in radians)
        float rotation = (float) Math.atan2(direction.y, direction.x);
        transform.setRotation(rotation);
    }
} 