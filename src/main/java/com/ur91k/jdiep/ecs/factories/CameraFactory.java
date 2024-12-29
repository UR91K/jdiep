package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import org.tinylog.Logger;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.joml.Vector2f;

public class CameraFactory {
    private final Engine engine;
    
    public CameraFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createCamera(Vector2f position) {
        Logger.debug("Creating camera at position: {}", position);
        
        Entity camera = engine.createEntity();
        
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        camera.add(transform);
        
        CameraComponent cameraComp = engine.createComponent(CameraComponent.class);
        camera.add(cameraComp);
        
        engine.addEntity(camera);
        Logger.debug("Camera entity created and added to engine");
        
        return camera;
    }
    
    public Entity createFollowCamera(Entity target, float followSpeed) {
        Entity camera = createCamera(new Vector2f(0, 0));
        
        // Configure camera to follow target
        CameraComponent cameraComp = camera.getComponent(CameraComponent.class);
        cameraComp.setTarget(target);
        cameraComp.setLerpFactor(followSpeed);
        
        Logger.debug("Created follow camera for target entity");
        return camera;
    }
} 