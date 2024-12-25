package com.ur91k.jdiep.debug.components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.ur91k.jdiep.engine.ecs.core.Component;

import java.util.function.Supplier;

/**
 * Component for rendering text labels in both world and screen space.
 * Can be used for both debug and normal text rendering.
 */
public class LabelComponent extends Component {
    private String text = "";
    private Vector2f offset = new Vector2f(0, 0);
    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private float scale = 1.0f;
    private boolean screenSpace = false;
    private boolean isDebug = false;
    private boolean autoUpdate = false;
    private Supplier<String> textSupplier = null;

    public LabelComponent() {}

    public String getText() {
        return autoUpdate && textSupplier != null ? textSupplier.get() : text;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public Vector4f getColor() {
        return color;
    }

    public float getScale() {
        return scale;
    }

    public boolean isScreenSpace() {
        return screenSpace;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    // Builder-style methods
    public LabelComponent setText(String text) {
        this.text = text;
        return this;
    }

    public LabelComponent setOffset(Vector2f offset) {
        this.offset = offset;
        return this;
    }

    public LabelComponent setOffset(float x, float y) {
        this.offset.set(x, y);
        return this;
    }

    public LabelComponent setColor(Vector4f color) {
        this.color = color;
        return this;
    }

    public LabelComponent setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
        return this;
    }

    public LabelComponent setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public LabelComponent setScreenSpace(boolean screenSpace) {
        this.screenSpace = screenSpace;
        return this;
    }

    public LabelComponent setDebug(boolean debug) {
        this.isDebug = debug;
        return this;
    }

    public LabelComponent setAutoUpdate(Supplier<String> supplier) {
        this.textSupplier = supplier;
        this.autoUpdate = supplier != null;
        return this;
    }
} 