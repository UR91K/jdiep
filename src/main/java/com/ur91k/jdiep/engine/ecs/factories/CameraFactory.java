package com.ur91k.jdiep.engine.ecs.factories;

import com.ur91k.jdiep.engine.core.logging.Logger;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.components.CameraComponent;

import org.joml.Vector2f;

public class CameraFactory implements EntityFactory {
    private static final Logger logger = Logger.getLogger(CameraFactory.class);
    private final World world;
    
    public CameraFactory(World world) {
        this.world = world;
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    public Entity createCamera(Vector2f position) {
        logger.debug("Creating camera at position: {}", position);
        
        Entity camera = world.createEntity();
        camera.addComponent(new TransformComponent());
        camera.getComponent(TransformComponent.class).setPosition(position);
        camera.addComponent(new CameraComponent());
        
        logger.debug("Created camera with {} components", camera.getComponents().size());
        return camera;
    }
} 