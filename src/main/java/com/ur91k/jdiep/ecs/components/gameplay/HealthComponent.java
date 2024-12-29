package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    private float health;
    private float maxHealth;
    private float regenRate;  // Health regenerated per second
    private float regenDelay; // Seconds before regeneration starts after damage
    private float lastDamageTime;
    private boolean isDead;
    
    public HealthComponent() {
        // Default constructor for Ashley's pooling
        this.health = 100.0f;
        this.maxHealth = 100.0f;
        this.regenRate = 0.0f;
        this.regenDelay = 5.0f;
        this.lastDamageTime = 0.0f;
        this.isDead = false;
    }
    
    public void init(float maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.regenRate = 0.0f;
        this.regenDelay = 5.0f;
        this.lastDamageTime = 0.0f;
        this.isDead = false;
    }
    
    public void init(float maxHealth, float regenRate, float regenDelay) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.regenRate = regenRate;
        this.regenDelay = regenDelay;
        this.lastDamageTime = 0.0f;
        this.isDead = false;
    }
    
    public void damage(float amount, float currentTime) {
        if (amount <= 0 || isDead) return;
        
        health -= amount;
        lastDamageTime = currentTime;
        
        if (health <= 0) {
            health = 0;
            isDead = true;
        }
    }
    
    public void heal(float amount) {
        if (amount <= 0 || isDead) return;
        
        health = Math.min(health + amount, maxHealth);
    }
    
    public void update(float currentTime, float deltaTime) {
        if (isDead || regenRate <= 0) return;
        
        if (currentTime - lastDamageTime >= regenDelay) {
            heal(regenRate * deltaTime);
        }
    }
    
    // Getters
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getHealthPercentage() { return health / maxHealth; }
    public boolean isDead() { return isDead; }
    public float getRegenRate() { return regenRate; }
    public float getRegenDelay() { return regenDelay; }
    
    // Setters
    public void setRegenRate(float rate) { this.regenRate = rate; }
    public void setRegenDelay(float delay) { this.regenDelay = delay; }
} 