package com.ur91k.jdiep.debug.factories;

import com.ur91k.jdiep.debug.components.*;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.World;
import com.ur91k.jdiep.core.window.Window;
import static com.ur91k.jdiep.graphics.config.RenderingConstants.rgb;

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
        COLOR_HITBOX = new Vector4f(rgb(0x0A910A)).mul(1, 1, 1, 0.5f),  // Semi-transparent green
        COLOR_DETECTION = new Vector4f(rgb(0x919100)).mul(1, 1, 1, 0.3f),  // Semi-transparent yellow
        COLOR_SPAWN = new Vector4f(rgb(0x0A0A91)).mul(1, 1, 1, 0.3f),  // Semi-transparent blue
        COLOR_AIM = rgb(0x910A91);  // Magenta
    
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
     * Creates combined debug visualization for an entity.
     */
    public DebugDrawComponent createDebugVisualizer(Entity entity, float hitboxRadius) {
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        
        DebugDrawComponent debugDraw = new DebugDrawComponent();
        
        // Add hitbox circle
        debugDraw.addDynamicCircle(
            () -> transform.getPosition(),
            () -> hitboxRadius,
            COLOR_HITBOX
        );
        
        // Add velocity vector - length represents actual velocity
        debugDraw.addDynamicLine(
            () -> transform.getPosition(),
            () -> {
                Vector2f endPos = new Vector2f(transform.getPosition());
                Vector2f velocity = movement.getVelocity();
                if (velocity.length() > 0.01f) { // Only show when moving
                    endPos.add(new Vector2f(velocity).mul(0.5f)); // Scale for visibility
                }
                return endPos;
            },
            COLOR_VELOCITY
        );
        
        // Add input direction vector - fixed length
        debugDraw.addDynamicLine(
            () -> transform.getPosition(),
            () -> {
                Vector2f endPos = new Vector2f(transform.getPosition());
                Vector2f input = movement.getInputDirection();
                if (input.length() > 0.01f) {  // Only show when there's input
                    endPos.add(new Vector2f(input).normalize().mul(40.0f));
                }
                return endPos;
            },
            COLOR_INPUT
        );
        
        return debugDraw;
    }
    
    /**
     * Creates spawner area visualization.
     */
    public DebugDrawComponent createSpawnerVisualizer(Vector2f position, float spawnRadius, float detectRadius) {
        DebugDrawComponent debugDraw = new DebugDrawComponent();
        
        // Spawn area
        debugDraw.addCircle(position, spawnRadius, COLOR_SPAWN);
        
        // Detection area
        debugDraw.addCircle(position, detectRadius, COLOR_DETECTION);
        
        return debugDraw;
    }
    
    /**
     * Creates weapon/turret visualization.
     */
    public DebugDrawComponent createTurretVisualizer(Entity turret) {
        TransformComponent transform = turret.getComponent(TransformComponent.class);
        
        DebugDrawComponent debugDraw = new DebugDrawComponent();

        debugDraw.addDynamicLine(
            () -> transform.getPosition(),
            () -> {
                Vector2f endPos = new Vector2f(transform.getPosition());
                float rotation = transform.getRotation();
                endPos.add(
                    new Vector2f(
                        (float)Math.cos(rotation),
                        (float)Math.sin(rotation)
                    ).mul(50.0f)
                );
                return endPos;
            },
            COLOR_AIM
        );
        
        return debugDraw;
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