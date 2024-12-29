package com.ur91k.jdiep.ecs.components.debug;

import com.badlogic.ashley.core.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import java.util.function.Supplier;

public class LabelComponent implements Component {
    private String text;
    private Vector2f offset;
    private Vector4f color;
    private float scale;
    private boolean screenSpace;
    private Supplier<String> textSupplier;
    
    public LabelComponent() {
        // Default constructor for Ashley's pooling
        this.text = "";
        this.offset = new Vector2f(0, 0);
        this.color = new Vector4f(1, 1, 1, 1);
        this.scale = 1.0f;
        this.screenSpace = false;
        this.textSupplier = null;
    }
    
    public void init(String text) {
        this.text = text;
        this.offset.set(0, 0);
        this.color.set(1, 1, 1, 1);
        this.scale = 1.0f;
        this.screenSpace = false;
        this.textSupplier = null;
    }
    
    public void init(String text, Vector2f offset, Vector4f color, float scale, boolean screenSpace) {
        this.text = text;
        this.offset.set(offset);
        this.color.set(color);
        this.scale = scale;
        this.screenSpace = screenSpace;
        this.textSupplier = null;
    }
    
    public String getText() {
        return textSupplier != null ? textSupplier.get() : text;
    }
    
    // Getters that return copies to maintain encapsulation
    public Vector2f getOffset() { return new Vector2f(offset); }
    public Vector4f getColor() { return new Vector4f(color); }
    public float getScale() { return scale; }
    public boolean isScreenSpace() { return screenSpace; }
    public boolean isDynamic() { return textSupplier != null; }
    
    // Setters with method chaining for convenience
    public LabelComponent setText(String text) {
        this.text = text;
        this.textSupplier = null;
        return this;
    }
    
    public LabelComponent setOffset(Vector2f offset) {
        this.offset.set(offset);
        return this;
    }
    
    public LabelComponent setOffset(float x, float y) {
        this.offset.set(x, y);
        return this;
    }
    
    public LabelComponent setColor(Vector4f color) {
        this.color.set(color);
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
    
    public LabelComponent setDynamic(Supplier<String> supplier) {
        this.textSupplier = supplier;
        if (supplier != null) {
            this.text = supplier.get(); // Initial value
        }
        return this;
    }
} 