package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class DroneComponent implements Component {
    private Entity owner;
    private float orbitRadius = 100.0f;  // Distance to maintain from owner
    private float orbitSpeed = 2.0f;     // Radians per second
    private float currentAngle = 0.0f;   // Current orbit angle
    
    public DroneComponent() {
        // Default constructor for Ashley's pooling
    }
    
    public void init(Entity owner) {
        this.owner = owner;
        this.currentAngle = (float)(Math.random() * Math.PI * 2);  // Random start angle
    }
    
    public Entity getOwner() {
        return owner;
    }
    
    public float getOrbitRadius() {
        return orbitRadius;
    }
    
    public void setOrbitRadius(float radius) {
        this.orbitRadius = radius;
    }
    
    public float getOrbitSpeed() {
        return orbitSpeed;
    }
    
    public void setOrbitSpeed(float speed) {
        this.orbitSpeed = speed;
    }
    
    public float getCurrentAngle() {
        return currentAngle;
    }
    
    public void setCurrentAngle(float angle) {
        this.currentAngle = angle;
    }
    
    public void updateAngle(float deltaTime) {
        currentAngle += orbitSpeed * deltaTime;
        if (currentAngle > Math.PI * 2) {
            currentAngle -= Math.PI * 2;
        }
    }
} 