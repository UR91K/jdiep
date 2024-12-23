package com.ur91k.jdiep.engine.ecs.firing;

public class TurretPhase {
    private final int phase;
    private final PhaseConfig config;
    
    public TurretPhase(PhaseConfig config, int phase) {
        if (phase < 1 || phase > config.getPhaseCount()) {
            throw new IllegalArgumentException(
                "Phase " + phase + " invalid for phase count: " + config.getPhaseCount()
            );
        }
        this.config = config;
        this.phase = phase;
    }
    
    public int getPhase() {
        return phase;
    }
    
    public PhaseConfig getConfig() {
        return config;
    }
    
    public float getFireTime() {
        return (phase - 1) * config.getPhaseInterval();
    }
} 