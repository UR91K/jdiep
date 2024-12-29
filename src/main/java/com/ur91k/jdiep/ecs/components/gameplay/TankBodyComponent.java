package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;
import com.ur91k.jdiep.game.config.TankScaling;
import com.ur91k.jdiep.game.weapons.PhaseConfig;

public class TankBodyComponent implements Component {
    private float mass;
    private float rotation;  // in radians
    private PhaseConfig phaseConfig;
    
    public TankBodyComponent() {
        // Default constructor for Ashley's pooling
        this.mass = 1.0f;
        this.rotation = 0.0f;
        this.phaseConfig = new PhaseConfig(1, 1.0f);
    }
    
    public void init(float mass, int phaseCount, float reloadTime) {
        this.mass = mass;
        this.rotation = 0.0f;
        this.phaseConfig = new PhaseConfig(phaseCount, reloadTime);
    }
    
    public float getMass() {
        return mass;
    }
    
    public void setMass(float mass) {
        if (mass <= 0) {
            throw new IllegalArgumentException("Mass must be positive");
        }
        this.mass = mass;
    }
    
    public float getDiameter() {
        return TankScaling.calculateDiameter(mass);
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    public float getRadius() {
        return getDiameter() / 2.0f;
    }
    
    public PhaseConfig getPhaseConfig() {
        return phaseConfig;
    }
} 