package com.ur91k.jdiep.engine.ecs.components;

import org.joml.Vector2f;

public class CameraComponent extends Component {
    public enum Mode {
        FOLLOW,     // Spring-based following
        FREE_ROAM,  // Direct control
        SPECTATE    // Following other players
    }

    private Vector2f position;
    private Vector2f velocity;
    private float springStiffness;
    private float damping;
    private float zoom;
    private Mode mode;

    public CameraComponent() {
        this.position = new Vector2f(0, 0);
        this.velocity = new Vector2f(0, 0);
        this.springStiffness = 30.0f;
        this.damping = 8.0f;
        this.zoom = 1.0f;
        this.mode = Mode.FOLLOW;
    }

    public Vector2f getPosition() { return position; }
    public Vector2f getVelocity() { return velocity; }
    public float getSpringStiffness() { return springStiffness; }
    public float getDamping() { return damping; }
    public float getZoom() { return zoom; }
    public Mode getMode() { return mode; }

    public void setPosition(Vector2f position) { this.position.set(position); }
    public void setVelocity(Vector2f velocity) { this.velocity.set(velocity); }
    public void setZoom(float zoom) { this.zoom = zoom; }
    public void setMode(Mode mode) { this.mode = mode; }
} 