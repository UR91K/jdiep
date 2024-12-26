package com.ur91k.jdiep.debug.components.visualizers;

import com.ur91k.jdiep.ecs.core.Component;
import org.joml.Vector4f;

public class SpawnerVisualizerComponent extends Component {
    private final Vector4f spawnColor;
    private final Vector4f detectionColor;
    private final float spawnRadius;
    private final float detectionRadius;
    private boolean visible = true;

    public SpawnerVisualizerComponent(Vector4f spawnColor, Vector4f detectionColor, float spawnRadius, float detectionRadius) {
        this.spawnColor = new Vector4f(spawnColor);
        this.detectionColor = new Vector4f(detectionColor);
        this.spawnRadius = spawnRadius;
        this.detectionRadius = detectionRadius;
    }

    public Vector4f getSpawnColor() {
        return new Vector4f(spawnColor);
    }

    public Vector4f getDetectionColor() {
        return new Vector4f(detectionColor);
    }

    public float getSpawnRadius() {
        return spawnRadius;
    }

    public float getDetectionRadius() {
        return detectionRadius;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
} 