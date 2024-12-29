package com.ur91k.jdiep.ecs.components.camera;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class CameraComponent implements Component {
    private Entity target;  // Entity to follow (optional)
    private float lerpFactor;  // Smoothing factor for camera movement
    private float zoom;
    private float minZoom;
    private float maxZoom;
    private float zoomSpeed;
    
    public CameraComponent() {
        // Default constructor for Ashley's pooling
        this.target = null;
        this.lerpFactor = 0.1f;  // Default smooth follow
        this.zoom = 1.0f;
        this.minZoom = 0.5f;
        this.maxZoom = 2.0f;
        this.zoomSpeed = 0.1f;
    }
    
    public void init() {
        this.target = null;
        this.lerpFactor = 0.1f;
        this.zoom = 1.0f;
        this.minZoom = 0.5f;
        this.maxZoom = 2.0f;
        this.zoomSpeed = 0.1f;
    }
    
    // Target entity methods
    public Entity getTarget() { return target; }
    public void setTarget(Entity target) { this.target = target; }
    public boolean hasTarget() { return target != null; }
    
    // Movement smoothing
    public float getLerpFactor() { return lerpFactor; }
    public void setLerpFactor(float factor) { this.lerpFactor = factor; }
    
    // Zoom controls
    public float getZoom() { return zoom; }
    public void setZoom(float zoom) { 
        this.zoom = Math.min(Math.max(zoom, minZoom), maxZoom);
    }
    public void adjustZoom(float delta) {
        setZoom(zoom + delta * zoomSpeed);
    }
    
    // Zoom limits
    public float getMinZoom() { return minZoom; }
    public void setMinZoom(float min) { this.minZoom = min; }
    public float getMaxZoom() { return maxZoom; }
    public void setMaxZoom(float max) { this.maxZoom = max; }
    
    // Zoom speed
    public float getZoomSpeed() { return zoomSpeed; }
    public void setZoomSpeed(float speed) { this.zoomSpeed = speed; }
} 