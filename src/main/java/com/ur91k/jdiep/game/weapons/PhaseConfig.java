package com.ur91k.jdiep.game.weapons;

public class PhaseConfig {
    private final int phaseCount;
    private final float reloadTime;
    
    public PhaseConfig(int phaseCount, float reloadTime) {
        if (phaseCount < 1) throw new IllegalArgumentException("Phase count must be positive");
        if (reloadTime <= 0) throw new IllegalArgumentException("Reload time must be positive");
        this.phaseCount = phaseCount;
        this.reloadTime = reloadTime;
    }
    
    public int getPhaseCount() {
        return phaseCount;
    }
    
    public float getReloadTime() {
        return reloadTime;
    }
    
    public float getPhaseInterval() {
        return reloadTime / phaseCount;
    }
} 