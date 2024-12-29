package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class ProjectileComponent implements Component {
    private float damage;
    private Entity owner;  // Entity that fired this projectile
    private boolean piercing;  // Whether projectile continues after hitting
    private int pierceCount;   // Number of entities it can pierce through
    private boolean explosive;  // Whether it explodes on impact
    private float blastRadius; // Explosion radius if explosive
    
    public ProjectileComponent() {
        // Default constructor for Ashley's pooling
        this.damage = 10.0f;
        this.piercing = false;
        this.pierceCount = 0;
        this.explosive = false;
        this.blastRadius = 0.0f;
    }
    
    public void init(float damage) {
        this.damage = damage;
        this.owner = null;
        this.piercing = false;
        this.pierceCount = 0;
        this.explosive = false;
        this.blastRadius = 0.0f;
    }
    
    public void init(float damage, Entity owner) {
        this.damage = damage;
        this.owner = owner;
        this.piercing = false;
        this.pierceCount = 0;
        this.explosive = false;
        this.blastRadius = 0.0f;
    }
    
    public void makePiercing(int pierceCount) {
        this.piercing = true;
        this.pierceCount = pierceCount;
    }
    
    public void makeExplosive(float blastRadius) {
        this.explosive = true;
        this.blastRadius = blastRadius;
    }
    
    public void onHit() {
        if (piercing) {
            pierceCount--;
            if (pierceCount <= 0) {
                piercing = false;
            }
        }
    }
    
    // Getters
    public float getDamage() { return damage; }
    public Entity getOwner() { return owner; }
    public boolean isPiercing() { return piercing; }
    public int getPierceCount() { return pierceCount; }
    public boolean isExplosive() { return explosive; }
    public float getBlastRadius() { return blastRadius; }
    
    // Setters
    public void setDamage(float damage) { this.damage = damage; }
    public void setOwner(Entity owner) { this.owner = owner; }
} 