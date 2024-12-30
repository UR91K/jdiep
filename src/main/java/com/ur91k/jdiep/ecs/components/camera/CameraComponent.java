package com.ur91k.jdiep.ecs.components.camera;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import org.joml.Vector2f;

public class CameraComponent implements Component {
    private Entity target;  // Entity to follow (optional)
    private float lerpFactor;  // Smoothing factor for camera movement
    private float zoom;
    private float minZoom;
    private float maxZoom;
    private float zoomSpeed;
    
    // Spring properties
    private Vector2f velocity;
    private float springStiffness;  // k in F = -kx
    private float damping;          // c in F = -cv
    
    public CameraComponent() {
        // Default constructor for Ashley's pooling
        this.target = null;
        this.lerpFactor = 0.1f;
        this.zoom = 1.0f;  // 1.0 zoom means 1 meter = 30 pixels
        this.minZoom = 0.5f;  // Can zoom out to see more of the world
        this.maxZoom = 2.0f;  // Can zoom in for detail
        this.zoomSpeed = 0.1f;
        
        // Initialize spring properties
        this.velocity = new Vector2f();
        this.springStiffness = 20.0f;  // Reduced for smoother following in meter-based units
        this.damping = 8.0f;           // Adjusted damping for meter-based units
    }
    
    public void init() {
        this.target = null;
        this.lerpFactor = 0.1f;
        this.zoom = 1.0f;
        this.minZoom = 0.5f;
        this.maxZoom = 2.0f;
        this.zoomSpeed = 0.1f;
        
        this.velocity.zero();
        this.springStiffness = 20.0f;
        this.damping = 8.0f;
    }
    
    // Target entity methods
    public Entity getTarget() { return target; }
    public void setTarget(Entity target) { this.target = target; }
    public boolean hasTarget() { return target != null; }
    
    // Movement smoothing
    public float getLerpFactor() { return lerpFactor; }
    public void setLerpFactor(float factor) { this.lerpFactor = factor; }
    
    // Spring properties
    public Vector2f getVelocity() { return velocity; }
    public void setVelocity(Vector2f velocity) { this.velocity.set(velocity); }
    public float getSpringStiffness() { return springStiffness; }
    public void setSpringStiffness(float stiffness) { this.springStiffness = stiffness; }
    public float getDamping() { return damping; }
    public void setDamping(float damping) { this.damping = damping; }
    
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