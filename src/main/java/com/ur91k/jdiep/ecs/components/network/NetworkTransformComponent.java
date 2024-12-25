package com.ur91k.jdiep.ecs.components.network;

import org.joml.Vector2f;

import com.ur91k.jdiep.ecs.core.Component;

public class NetworkTransformComponent extends Component {
    private Vector2f lastReceivedPosition;
    private Vector2f targetPosition;
    private float lastUpdateTime;
    private float interpolationTime = 0.1f; // 100ms interpolation
    
    public NetworkTransformComponent() {
        this.lastReceivedPosition = new Vector2f();
        this.targetPosition = new Vector2f();
    }
    
    public void setNetworkPosition(Vector2f position, float timestamp) {
        this.lastReceivedPosition.set(this.targetPosition);
        this.targetPosition.set(position);
        this.lastUpdateTime = timestamp;
    }
    
    // Getters and interpolation methods
    public Vector2f getLastReceivedPosition() { return new Vector2f(lastReceivedPosition); }
    public Vector2f getTargetPosition() { return new Vector2f(targetPosition); }
    public float getLastUpdateTime() { return lastUpdateTime; }
    public float getInterpolationTime() { return interpolationTime; }
} 