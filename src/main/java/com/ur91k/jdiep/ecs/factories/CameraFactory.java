package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;

public class CameraFactory {
    private static final Logger logger = Logger.getLogger(CameraFactory.class);
    private final Engine engine;
    
    public CameraFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createCamera(Vector2f position) {
        logger.debug("Creating camera at position: {}", position);
        
        Entity camera = engine.createEntity();
        
        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        camera.add(transform);
        
        // Add camera component
        CameraComponent cameraComp = engine.createComponent(CameraComponent.class);
        camera.add(cameraComp);
        
        // Add to engine
        engine.addEntity(camera);
        
        logger.debug("Created camera entity");
        return camera;
    }
    
    public Entity createFollowCamera(Entity target, float followSpeed) {
        Entity camera = createCamera(new Vector2f(0, 0));
        
        // Configure camera to follow target
        CameraComponent cameraComp = camera.getComponent(CameraComponent.class);
        cameraComp.setTarget(target);
        cameraComp.setLerpFactor(followSpeed);
        
        logger.debug("Created follow camera for target entity");
        return camera;
    }
} 