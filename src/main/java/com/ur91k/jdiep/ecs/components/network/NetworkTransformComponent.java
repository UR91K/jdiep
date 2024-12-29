package com.ur91k.jdiep.ecs.components.network;

import org.joml.Vector2f;
import com.badlogic.ashley.core.Component;

public class NetworkTransformComponent implements Component {
    private Vector2f lastReceivedPosition;
    private Vector2f targetPosition;
    private float lastUpdateTime;
    private float interpolationTime = 0.1f; // 100ms interpolation
    
    public NetworkTransformComponent() {
        // Default constructor for Ashley's pooling
        this.lastReceivedPosition = new Vector2f();
        this.targetPosition = new Vector2f();
        this.lastUpdateTime = 0.0f;
    }
    
    public void init(Vector2f position, float timestamp) {
        this.lastReceivedPosition.set(position);
        this.targetPosition.set(position);
        this.lastUpdateTime = timestamp;
        this.interpolationTime = 0.1f;
    }
    
    public void setNetworkPosition(Vector2f position, float timestamp) {
        this.lastReceivedPosition.set(this.targetPosition);
        this.targetPosition.set(position);
        this.lastUpdateTime = timestamp;
    }
    
    public void setInterpolationTime(float time) {
        this.interpolationTime = time;
    }
    
    // Getters and interpolation methods
    public Vector2f getLastReceivedPosition() { return new Vector2f(lastReceivedPosition); }
    public Vector2f getTargetPosition() { return new Vector2f(targetPosition); }
    public float getLastUpdateTime() { return lastUpdateTime; }
    public float getInterpolationTime() { return interpolationTime; }
    
    /**
     * Calculate interpolated position based on current time
     */
    public Vector2f getInterpolatedPosition(float currentTime) {
        float timeSinceUpdate = currentTime - lastUpdateTime;
        float t = Math.min(timeSinceUpdate / interpolationTime, 1.0f);
        
        return new Vector2f(lastReceivedPosition).lerp(targetPosition, t);
    }
} 