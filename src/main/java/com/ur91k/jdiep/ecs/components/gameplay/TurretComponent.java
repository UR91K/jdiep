package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Component that represents a turret's gameplay properties.
 * The turret itself is an entity with its own physics body, and this component
 * holds the gameplay-related data and tank relationship.
 */
public class TurretComponent implements Component {
    private Entity tankBody;       // Reference to the parent tank entity
    private float width;          // Turret width in meters
    private float length;         // Turret length in meters
    private float reloadTime;     // Time between shots in seconds
    private float recoilForce;    // Force applied when shooting in Newtons
    private int phase;            // Firing phase for multi-turret setups
    private float lastShotTime;   // Time of last shot for reload tracking
    
    public TurretComponent() {
        this.width = 1.0f;
        this.length = 2.0f;
        this.reloadTime = 1.0f;
        this.recoilForce = 100.0f;
        this.phase = 1;
        this.lastShotTime = 0.0f;
    }
    
    public void init(Entity tankBody, float width, float length, float reloadTime, float recoilForce, int phase) {
        this.tankBody = tankBody;
        this.width = width;
        this.length = length;
        this.reloadTime = reloadTime;
        this.recoilForce = recoilForce;
        this.phase = phase;
        this.lastShotTime = 0.0f;
    }
    
    public Entity getTankBody() {
        return tankBody;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getLength() {
        return length;
    }
    
    public float getReloadTime() {
        return reloadTime;
    }
    
    public float getRecoilForce() {
        return recoilForce;
    }
    
    public int getPhase() {
        return phase;
    }
    
    public float getLastShotTime() {
        return lastShotTime;
    }
    
    public void setLastShotTime(float time) {
        this.lastShotTime = time;
    }
    
    public boolean canShoot(float currentTime) {
        return currentTime - lastShotTime >= reloadTime;
    }
} 