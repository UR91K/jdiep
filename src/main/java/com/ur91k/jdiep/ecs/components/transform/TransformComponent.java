package com.ur91k.jdiep.ecs.components.transform;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;

public class TransformComponent implements Component {
    private Vector2f position;
    private Vector2f scale;
    private float rotation;

    public TransformComponent() {
        this.position = new Vector2f();
        this.scale = new Vector2f(1.0f, 1.0f);
        this.rotation = 0.0f;
    }

    public TransformComponent(Vector2f position) {
        this();
        this.position.set(position);
    }

    public TransformComponent(Vector2f position, Vector2f scale) {
        this(position);
        this.scale.set(scale);
    }

    public TransformComponent(Vector2f position, Vector2f scale, float rotation) {
        this(position, scale);
        this.rotation = rotation;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale.set(scale);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
} 