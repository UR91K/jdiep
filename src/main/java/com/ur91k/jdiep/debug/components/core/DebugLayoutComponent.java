package com.ur91k.jdiep.debug.components.core;

import com.ur91k.jdiep.ecs.core.Component;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.core.window.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Component that manages the layout of debug UI elements.
 * Positions graphs in a grid from the top-right corner and
 * monitors in a vertical stack from the top-left corner.
 */
public class DebugLayoutComponent extends Component {
    // Fixed dimensions and spacing
    public static final int PADDING = 10;                    // Padding between elements and screen edges
    public static final int GRAPH_WIDTH = DebugGraphComponent.DEFAULT_WIDTH;
    public static final int GRAPH_HEIGHT = DebugGraphComponent.DEFAULT_HEIGHT;
    public static final int FONT_WIDTH = 8;                 // BDF font dimensions
    public static final int FONT_HEIGHT = 16;
    public static final int MONITOR_LINE_HEIGHT = 20;       // Font height + 4px spacing
    public static final int GRAPH_COLUMNS = 3;              // Maximum number of graph columns

    private final List<Entity> graphEntities = new ArrayList<>();
    private final List<Entity> monitorEntities = new ArrayList<>();
    private final Window window;

    public DebugLayoutComponent(Window window) {
        this.window = window;
    }

    /**
     * Called when window is resized to update layout
     */
    public void handleResize(int width, int height) {
        updateLayout();
    }

    /**
     * Adds a graph entity to be managed by the layout system.
     * Updates positions of all graphs immediately.
     */
    public void addGraph(Entity graphEntity) {
        if (!graphEntity.hasComponent(DebugGraphComponent.class)) {
            throw new IllegalArgumentException("Entity must have a DebugGraphComponent");
        }
        graphEntities.add(graphEntity);
        updateLayout();
    }

    /**
     * Adds a monitor entity to be managed by the layout system.
     * Updates positions of all monitors immediately.
     */
    public void addMonitor(Entity monitorEntity) {
        if (!monitorEntity.hasComponent(LabelComponent.class)) {
            throw new IllegalArgumentException("Entity must have a LabelComponent");
        }
        monitorEntities.add(monitorEntity);
        updateLayout();
    }

    /**
     * Removes a graph from the layout system.
     */
    public void removeGraph(Entity graphEntity) {
        graphEntities.remove(graphEntity);
        updateLayout();
    }

    /**
     * Removes a monitor from the layout system.
     */
    public void removeMonitor(Entity monitorEntity) {
        monitorEntities.remove(monitorEntity);
        updateLayout();
    }

    /**
     * Updates positions of all managed UI elements.
     * Called automatically when elements are added or removed.
     */
    private void updateLayout() {
        updateGraphLayout();
        updateMonitorLayout();
    }

    private void updateGraphLayout() {
        float screenWidth = window.getWidth();
        
        for (int i = 0; i < graphEntities.size(); i++) {
            Entity graphEntity = graphEntities.get(i);
            DebugGraphComponent graph = graphEntity.getComponent(DebugGraphComponent.class);
            
            // Calculate grid position (from top-right)
            // First fill vertically, then move left
            int column = i / GRAPH_COLUMNS;  // How many full columns we've filled
            int row = i % GRAPH_COLUMNS;     // Position within current column
            
            // Calculate pixel offsets from top-right corner
            float x = screenWidth - GRAPH_WIDTH - PADDING - (column * (GRAPH_WIDTH + PADDING));
            float y = PADDING + (row * (GRAPH_HEIGHT + PADDING));
            
            // Update graph position (stored as offset from top-right)
            graph.getFixedScreenPosition().set(x, y);
        }
    }

    private void updateMonitorLayout() {
        for (int i = 0; i < monitorEntities.size(); i++) {
            Entity monitorEntity = monitorEntities.get(i);
            LabelComponent label = monitorEntity.getComponent(LabelComponent.class);
            
            // Calculate position from top-left
            float x = PADDING;
            float y = PADDING + (i * MONITOR_LINE_HEIGHT);
            
            // Update monitor position
            label.setOffset(x, y);
        }
    }
} 