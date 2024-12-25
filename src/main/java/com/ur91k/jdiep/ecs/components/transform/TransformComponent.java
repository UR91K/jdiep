package com.ur91k.jdiep.ecs.components.transform;

import org.joml.Vector2f;

import com.ur91k.jdiep.ecs.core.Component;
import com.ur91k.jdiep.ecs.core.Entity;

public class TransformComponent extends Component {
    private Vector2f position;
    private Vector2f scale;
    private float rotation; // In radians
    private Entity entity;  // Reference to owning entity

    public TransformComponent() {
        this(new Vector2f(), new Vector2f(1.0f, 1.0f), 0.0f);
    }

    public TransformComponent(Vector2f position, Vector2f scale, float rotation) {
        this.position = new Vector2f(position);
        this.scale = new Vector2f(scale);
        this.rotation = rotation;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public Vector2f getPosition() {
        return new Vector2f(position);
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public void translate(Vector2f delta) {
        this.position.add(delta);
    }

    public Vector2f getScale() {
        return new Vector2f(scale);
    }

    public void setScale(Vector2f scale) {
        this.scale.set(scale);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        // Normalize rotation to [0, 2π)
        this.rotation = (float)((rotation % (2 * Math.PI) + 2 * Math.PI) % (2 * Math.PI));
    }

    public void rotate(float deltaRadians) {
        setRotation(rotation + deltaRadians);
    }

    // Get the forward direction vector based on rotation
    public Vector2f getForward() {
        return new Vector2f(
            (float)Math.cos(rotation),
            (float)Math.sin(rotation)
        );
    }

    // Get the right direction vector based on rotation
    public Vector2f getRight() {
        return new Vector2f(
            (float)Math.cos(rotation + Math.PI/2),
            (float)Math.sin(rotation + Math.PI/2)
        );
    }

    @Override
    public String toString() {
        return String.format("Transform(pos=%s, scale=%s, rot=%.2f)", position, scale, rotation);
    }
} 