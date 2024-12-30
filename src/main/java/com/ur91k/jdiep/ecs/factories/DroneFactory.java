package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.gameplay.DroneComponent;
import com.ur91k.jdiep.ecs.components.gameplay.DroneControllerComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.tinylog.Logger;

public class DroneFactory {
    private final Engine engine;
    
    public DroneFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createDrone(Vector2f position, Entity owner) {
        Entity drone = engine.createEntity();
        
        // Add transform
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        drone.add(transform);
        
        // Add drone component
        DroneComponent droneComp = engine.createComponent(DroneComponent.class);
        droneComp.init(owner);
        drone.add(droneComp);
        
        // Add drone controller for physics-based movement
        DroneControllerComponent controller = engine.createComponent(DroneControllerComponent.class);
        controller.setMaxForce(400.0f);  // Adjust for desired acceleration
        controller.setMaxTorque(200.0f); // Adjust for desired rotation speed
        drone.add(controller);
        
        // Add collision (octagon shape for drones)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float radius = 15.0f;  // Size of drone
        Vector2f[] vertices = createOctagonVertices(radius);
        collision.init(drone, vertices, CollisionFilters.CATEGORY_DRONE, CollisionFilters.MASK_DRONE);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.3f);  // Lighter than tanks
        collision.setFriction(0.1f);
        collision.setRestitution(0.3f);
        collision.setLinearDamping(0.5f);
        collision.setAngularDamping(0.8f);
        collision.setBullet(true);  // Enable continuous collision detection
        drone.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(vertices);  // Octagon shape
        drone.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS);
        drone.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.DRONE_FILL_COLOR);
        color.setOutline(RenderingConstants.DRONE_OUTLINE_COLOR, 2.0f);
        drone.add(color);
        
        engine.addEntity(drone);
        Logger.debug("Created drone at position: {} for owner: {}", position, owner);
        return drone;
    }
    
    private Vector2f[] createOctagonVertices(float radius) {
        Vector2f[] vertices = new Vector2f[8];
        for (int i = 0; i < 8; i++) {
            float angle = (float) (2 * Math.PI * i / 8);
            vertices[i] = new Vector2f(
                (float) (radius * Math.cos(angle)),
                (float) (radius * Math.sin(angle))
            );
        }
        return vertices;
    }
} 