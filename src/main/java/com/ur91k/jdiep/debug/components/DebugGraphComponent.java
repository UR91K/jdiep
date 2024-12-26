package com.ur91k.jdiep.debug.components;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.ur91k.jdiep.ecs.core.Component;

/**
 * Component for rendering real-time data graphs.
 * Supports auto-scaling and circular buffer for continuous data.
 */
public class DebugGraphComponent extends Component {
    public static final int DEFAULT_WIDTH = 176;  // Fixed pixel width
    public static final int DEFAULT_HEIGHT = 101; // Fixed pixel height
    
    private final String id;
    private final Vector2f screenPosition;  // Position relative to top-right corner
    private final float[] values;
    private int currentIndex = 0;
    private int valueCount = 0;  // Track how many values we've added
    private float minValue = 0;
    private float maxValue = 1;
    private boolean autoScale = true;
    private Vector4f color = new Vector4f(0, 1, 0, 0.8f);
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private String label = "";
    private boolean visible = true;
    private float defaultValue = 0.0f;

    /**
     * Creates a new debug graph component.
     * @param id Unique identifier for the graph
     * @param screenPosition Position in pixels from the top-right corner of the screen
     * @param maxSamples Maximum number of samples to store
     */
    public DebugGraphComponent(String id, Vector2f screenPosition, int maxSamples) {
        this.id = id;
        // Store position as offset from top-right corner
        this.screenPosition = new Vector2f(screenPosition);
        this.values = new float[maxSamples];
        // Initialize array with default value
        for (int i = 0; i < maxSamples; i++) {
            values[i] = defaultValue;
        }
    }

    /**
     * Gets the fixed screen position in pixels from the top-right corner.
     * @return Vector2f containing the x,y pixel coordinates from top-right
     */
    public Vector2f getFixedScreenPosition() {
        return new Vector2f(screenPosition);
    }

    public void addValue(float value) {
        // Store the value
        values[currentIndex] = value;
        currentIndex = (currentIndex + 1) % values.length;
        valueCount = Math.min(valueCount + 1, values.length);

        if (autoScale && valueCount > 0) {
            // Only scan values we've actually added
            minValue = Float.MAX_VALUE;
            maxValue = Float.MIN_VALUE;
            int count = Math.min(valueCount, values.length);
            
            for (int i = 0; i < count; i++) {
                float v = values[i];
                minValue = Math.min(minValue, v);
                maxValue = Math.max(maxValue, v);
            }
            
            // Ensure we always have a valid range
            if (minValue == maxValue) {
                float currentValue = minValue;
                minValue = currentValue - 0.5f;
                maxValue = currentValue + 0.5f;
            }
        }
    }

    // Getters
    public String getId() { return id; }
    public Vector2f getScreenPosition() { return new Vector2f(screenPosition); }
    public float[] getValues() { return values; }
    public int getCurrentIndex() { return currentIndex; }
    public int getValueCount() { return valueCount; }
    public float getMinValue() { return minValue; }
    public float getMaxValue() { return maxValue; }
    public boolean isAutoScale() { return autoScale; }
    public Vector4f getColor() { return new Vector4f(color); }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getLabel() { return label; }
    public boolean isVisible() { return visible; }
    public float getDefaultValue() { return defaultValue; }

    // Builder-style setters
    public DebugGraphComponent setColor(Vector4f color) {
        this.color.set(color);
        return this;
    }

    public DebugGraphComponent setDimensions(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        return this;
    }

    public DebugGraphComponent setLabel(String label) {
        this.label = label != null ? label : "";
        return this;
    }

    public DebugGraphComponent setAutoScale(boolean autoScale) {
        this.autoScale = autoScale;
        return this;
    }

    public DebugGraphComponent setRange(float min, float max) {
        if (!autoScale && min < max) {
            this.minValue = min;
            this.maxValue = max;
        }
        return this;
    }

    public DebugGraphComponent setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public DebugGraphComponent setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
        // Reset all unwritten values to new default
        for (int i = valueCount; i < values.length; i++) {
            values[i] = defaultValue;
        }
        return this;
    }

    /**
     * Clears all values and resets to initial state.
     */
    public void clear() {
        currentIndex = 0;
        valueCount = 0;
        for (int i = 0; i < values.length; i++) {
            values[i] = defaultValue;
        }
        if (!autoScale) {
            minValue = 0;
            maxValue = 1;
        }
    }
}