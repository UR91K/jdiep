package com.ur91k.jdiep.debug.factories;

import com.ur91k.jdiep.debug.components.*;
import com.ur91k.jdiep.debug.components.core.DebugGraphComponent;
import com.ur91k.jdiep.debug.components.core.DebugLayoutComponent;
import com.ur91k.jdiep.debug.components.core.LabelComponent;
import com.ur91k.jdiep.debug.components.visualizers.HitboxVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.InputVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.SpawnerVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.TurretVisualizerComponent;
import com.ur91k.jdiep.debug.components.visualizers.VelocityVisualizerComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.core.window.Window;
import static com.ur91k.jdiep.core.utils.ColorUtils.rgb;
import static com.ur91k.jdiep.core.utils.ColorUtils.rgba;

import org.joml.Vector2f;
import org.joml.Vector4f;
import java.util.function.Supplier;

/**
 * Factory for creating common debug visualizations.
 */
@SuppressWarnings("unused")
public class DebugFactory {
    // Debug colors
    private static final Vector4f 
        COLOR_DEBUG = rgb(0x0A9371),    // Default debug color
        COLOR_VELOCITY = rgb(0x910A2E),  // Red
        COLOR_INPUT = rgb(0x0A91E3),     // Light blue
        COLOR_HITBOX = rgba(0x0A910A80),  // Semi-transparent green
        COLOR_DETECTION = rgba(0x91910050),  // Semi-transparent yellow
        COLOR_SPAWN = rgba(0x0A0A9150),   // Semi-transparent blue
        COLOR_AIM = rgb(0x910A91);        // Magenta
    
    private final World world;
    private final Window window;
    private Entity layoutEntity;
    
    public DebugFactory(World world, Window window) {
        this.world = world;
        this.window = window;
        
        // Create the layout entity
        this.layoutEntity = world.createEntity();
        this.layoutEntity.addComponent(new DebugLayoutComponent(window));
    }
    
    /**
     * Creates a debug label that follows an entity.
     */
    @SuppressWarnings("unused")
    public Entity createEntityLabel(Entity target, String text) {
        Entity label = world.createEntity();
        
        TransformComponent targetTransform = target.getComponent(TransformComponent.class);
        Vector2f offset = new Vector2f(0, 30); // Above the entity
        
        // Add transform component to track target's position
        TransformComponent labelTransform = new TransformComponent();
        labelTransform.setPosition(new Vector2f(0, 30)); // Offset from parent
        label.addComponent(labelTransform);
        
        // Make label follow target
        label.addComponent(new ParentComponent(target));
        
        label.addComponent(new LabelComponent()
            .setText(text)
            .setColor(COLOR_DEBUG)
            .setDebug(true)
            .setScreenSpace(false));  // Explicitly set to world space
            
        return label;
    }
    
    /**
     * Creates a debug monitor that displays a value that updates each frame.
     */
    public Entity createDebugMonitor(String prefix, Supplier<String> valueSupplier) {
        Entity monitor = world.createEntity();
        
        monitor.addComponent(new LabelComponent()
            .setAutoUpdate(() -> prefix + ": " + valueSupplier.get())
            .setColor(COLOR_DEBUG)
            .setScreenSpace(true)
            .setDebug(true));
            
        // Add to layout
        layoutEntity.getComponent(DebugLayoutComponent.class)
            .addMonitor(monitor);
            
        return monitor;
    }
    
    /**
     * Creates velocity visualization component.
     */
    public VelocityVisualizerComponent createVelocityVisualizer() {
        return new VelocityVisualizerComponent(COLOR_VELOCITY, 0.5f);
    }
    
    /**
     * Creates input direction visualization component.
     */
    public InputVisualizerComponent createInputVisualizer() {
        return new InputVisualizerComponent(COLOR_INPUT, 40.0f);
    }
    
    /**
     * Creates hitbox visualization component.
     */
    public HitboxVisualizerComponent createHitboxVisualizer(float radius) {
        return new HitboxVisualizerComponent(COLOR_HITBOX, radius);
    }
    
    /**
     * Creates spawner visualization component.
     */
    public SpawnerVisualizerComponent createSpawnerVisualizer(float spawnRadius, float detectRadius) {
        return new SpawnerVisualizerComponent(COLOR_SPAWN, COLOR_DETECTION, spawnRadius, detectRadius);
    }
    
    /**
     * Creates turret visualization component.
     */
    public TurretVisualizerComponent createTurretVisualizer() {
        return new TurretVisualizerComponent(COLOR_AIM, 50.0f);
    }
    
    /**
     * Creates performance graph.
     */
    public Entity createPerformanceGraph(String id) {
        Entity graph = world.createEntity();
        
        graph.addComponent(new DebugGraphComponent(id, new Vector2f(0, 0), 100)  // Position will be set by layout
            .setColor(COLOR_DEBUG)
            .setDimensions(DebugGraphComponent.DEFAULT_WIDTH, DebugGraphComponent.DEFAULT_HEIGHT));
            
        layoutEntity.getComponent(DebugLayoutComponent.class)
            .addGraph(graph);
            
        return graph;
    }
    
    /**
     * Gets the debug layout component that manages UI positioning.
     */
    public DebugLayoutComponent getLayoutComponent() {
        return layoutEntity.getComponent(DebugLayoutComponent.class);
    }
} 