package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.factories.DroneFactory.DroneType;

public class DroneComponent implements Component {
    private DroneType type;
    private Entity owner;  // For team-based drones
    private float attackRange;
    private float attackDamage;
    private float attackSpeed;  // Attacks per second
    private float lastAttackTime;
    private boolean isAggressive;
    
    public DroneComponent() {
        // Default constructor for Ashley's pooling
        this.type = DroneType.GUARD;
        this.attackRange = 200.0f;
        this.attackDamage = 10.0f;
        this.attackSpeed = 1.0f;
        this.lastAttackTime = 0.0f;
        this.isAggressive = false;
    }
    
    public void init(DroneType type) {
        this.type = type;
        this.owner = null;
        
        // Configure based on type
        switch (type) {
            case GUARD:
                this.attackRange = 200.0f;
                this.attackDamage = 10.0f;
                this.attackSpeed = 1.0f;
                this.isAggressive = true;
                break;
            case BASE:
                this.attackRange = 300.0f;
                this.attackDamage = 15.0f;
                this.attackSpeed = 1.5f;
                this.isAggressive = true;
                break;
            case OVERLORD:
                this.attackRange = 250.0f;
                this.attackDamage = 20.0f;
                this.attackSpeed = 2.0f;
                this.isAggressive = false;
                break;
            case NECRO:
                this.attackRange = 150.0f;
                this.attackDamage = 5.0f;
                this.attackSpeed = 0.5f;
                this.isAggressive = true;
                break;
        }
        
        this.lastAttackTime = 0.0f;
    }
    
    public boolean canAttack(float currentTime) {
        return currentTime - lastAttackTime >= 1.0f / attackSpeed;
    }
    
    public void attack(float currentTime) {
        lastAttackTime = currentTime;
    }
    
    // Getters
    public DroneType getType() { return type; }
    public Entity getOwner() { return owner; }
    public float getAttackRange() { return attackRange; }
    public float getAttackDamage() { return attackDamage; }
    public float getAttackSpeed() { return attackSpeed; }
    public boolean isAggressive() { return isAggressive; }
    
    // Setters
    public void setOwner(Entity owner) { this.owner = owner; }
    public void setAggressive(boolean aggressive) { this.isAggressive = aggressive; }
    public void setAttackDamage(float damage) { this.attackDamage = damage; }
    public void setAttackSpeed(float speed) { this.attackSpeed = speed; }
} 