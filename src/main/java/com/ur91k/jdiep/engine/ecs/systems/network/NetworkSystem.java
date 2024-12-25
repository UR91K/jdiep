package com.ur91k.jdiep.engine.ecs.systems.network;

import com.ur91k.jdiep.core.time.Time;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerComponent;
import com.ur91k.jdiep.ecs.components.network.NetworkTransformComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.System;

import org.joml.Vector2f;

@SuppressWarnings("unused")
public class NetworkSystem extends System {
    private static final float NETWORK_SEND_RATE = 1.0f / 20.0f; // 20Hz update rate
    private float timeSinceLastUpdate = 0;
    
    @Override
    public void update() {
        timeSinceLastUpdate += Time.getDeltaTime();
        
        // Update remote players' positions through interpolation
        interpolateRemotePlayers();
        
        // Send local player state to network
        if (timeSinceLastUpdate >= NETWORK_SEND_RATE) {
            sendLocalPlayerState();
            timeSinceLastUpdate = 0;
        }
    }
    
    private void interpolateRemotePlayers() {
        var remotePlayers = world.getEntitiesWith(
            PlayerComponent.class,
            TransformComponent.class,
            NetworkTransformComponent.class
        );
        
        float currentTime = (float)Time.getCurrentTime();
        
        for (Entity player : remotePlayers) {
            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
            if (playerComp.isLocalPlayer()) continue;
            
            TransformComponent transform = player.getComponent(TransformComponent.class);
            NetworkTransformComponent netTransform = player.getComponent(NetworkTransformComponent.class);
            
            float t = Math.min(1.0f, 
                (currentTime - netTransform.getLastUpdateTime()) / 
                netTransform.getInterpolationTime()
            );
            
            // Interpolate between last and target positions
            Vector2f interpolatedPos = new Vector2f(
                netTransform.getLastReceivedPosition().lerp(netTransform.getTargetPosition(), t)
            );
            
            transform.setPosition(interpolatedPos);
        }
    }
    
    private void sendLocalPlayerState() {
        var localPlayers = world.getEntitiesWith(
            PlayerComponent.class,
            TransformComponent.class
        );
        
        for (Entity player : localPlayers) {
            PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
            if (!playerComp.isLocalPlayer()) continue;
            
            TransformComponent transform = player.getComponent(TransformComponent.class);
            // TODO: Send position and other state to network
            // networkManager.sendPlayerState(playerComp.getPlayerId(), transform.getPosition());
        }
    }
} 