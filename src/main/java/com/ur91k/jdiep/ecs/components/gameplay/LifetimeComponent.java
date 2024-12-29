package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;

public class LifetimeComponent implements Component {
    private float lifetime;      // Total lifetime in seconds
    private float timeRemaining; // Time remaining in seconds
    private boolean isPaused;    // Whether lifetime countdown is paused
    private boolean isExpired;   // Whether lifetime has expired
    private Runnable onExpire;   // Optional callback when lifetime expires
    
    public LifetimeComponent() {
        // Default constructor for Ashley's pooling
        this.lifetime = 1.0f;
        this.timeRemaining = 1.0f;
        this.isPaused = false;
        this.isExpired = false;
        this.onExpire = null;
    }
    
    public void init(float lifetime) {
        this.lifetime = lifetime;
        this.timeRemaining = lifetime;
        this.isPaused = false;
        this.isExpired = false;
        this.onExpire = null;
    }
    
    public void init(float lifetime, Runnable onExpire) {
        this.lifetime = lifetime;
        this.timeRemaining = lifetime;
        this.isPaused = false;
        this.isExpired = false;
        this.onExpire = onExpire;
    }
    
    public void update(float deltaTime) {
        if (isExpired || isPaused) return;
        
        timeRemaining -= deltaTime;
        
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            isExpired = true;
            if (onExpire != null) {
                onExpire.run();
            }
        }
    }
    
    public void reset() {
        timeRemaining = lifetime;
        isExpired = false;
    }
    
    public void pause() {
        isPaused = true;
    }
    
    public void resume() {
        isPaused = false;
    }
    
    // Getters
    public float getLifetime() { return lifetime; }
    public float getTimeRemaining() { return timeRemaining; }
    public float getProgress() { return 1.0f - (timeRemaining / lifetime); }
    public boolean isPaused() { return isPaused; }
    public boolean isExpired() { return isExpired; }
    
    // Setters
    public void setOnExpire(Runnable callback) { this.onExpire = callback; }
    public void setLifetime(float lifetime) { 
        this.lifetime = lifetime;
        this.timeRemaining = lifetime;
    }
} 