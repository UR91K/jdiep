package com.ur91k.jdiep.debug.factories;

import com.ur91k.jdiep.debug.components.DebugDrawComponent;
import com.ur91k.jdiep.debug.components.DebugGraphComponent;
import com.ur91k.jdiep.debug.components.LabelComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.ecs.core.Entity;
import com.ur91k.jdiep.ecs.core.World;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.function.Supplier;

/**
 * Factory for creating common debug visualizations.
 */
public class DebugFactory {
    // Debug colors
    private static final Vector4f 
        COLOR_DEBUG = new Vector4f(0.0f, 1.0f, 0.0f, 0.8f),    // Default debug color
        COLOR_VELOCITY = new Vector4f(1, 0, 0, 1),             // Red
        COLOR_HITBOX = new Vector4f(0, 1, 0, 0.5f),           // Semi-transparent green
        COLOR_DETECTION = new Vector4f(1, 1, 0, 0.3f),        // Semi-transparent yellow
        COLOR_SPAWN = new Vector4f(0, 0, 1, 0.3f),            // Semi-transparent blue
        COLOR_AIM = new Vector4f(1, 0, 1, 1);                 // Magenta
    
    private final World world;
    
    public DebugFactory(World world) {
        this.world = world;
    }
    
    /**
     * Creates a debug label that follows an entity.
     */
    @SuppressWarnings("unused")
    public Entity createEntityLabel(Entity target, String text) {
        Entity label = world.createEntity();
        
        TransformComponent targetTransform = target.getComponent(TransformComponent.class);
        Vector2f offset = new Vector2f(0, 30); // Above the entity
        
        label.addComponent(new LabelComponent()
            .setText(text)
            .setOffset(offset)
            .setColor(COLOR_DEBUG)
            .setDebug(true));
            
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
            
        return monitor;
    }
    
    /**
     * Creates velocity visualization for an entity.
     */
    public DebugDrawComponent createVelocityVisualizer(Entity entity) {
        MovementComponent movement = entity.getComponent(MovementComponent.class);
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        
        DebugDrawComponent debugDraw = new DebugDrawComponent();
        
        // Add velocity vector
        debugDraw.addDynamicLine(
            () -> transform.getPosition(),
            () -> {
                Vector2f endPos = new Vector2f(transform.getPosition());
                Vector2f velocity = movement.getVelocity();
                endPos.add(new Vector2f(velocity).normalize().mul(velocity.length() * 50)); // Scale for visibility
                return endPos;
            },
            COLOR_VELOCITY
        );
        
        return debugDraw;
    }
    
    /**
     * Creates hitbox visualization for an entity.
     */
    public DebugDrawComponent createHitboxVisualizer(Entity entity, float radius) {
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        
        DebugDrawComponent debugDraw = new DebugDrawComponent();
        
        // Add hitbox circle
        debugDraw.addDynamicCircle(
            () -> transform.getPosition(),
            () -> radius,
            COLOR_HITBOX
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
        
        // Add aim line
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
    public Entity createPerformanceGraph(String id, Vector2f position) {
        Entity graph = world.createEntity();
        
        graph.addComponent(new DebugGraphComponent(id, position, 100)
            .setColor(COLOR_DEBUG)
            .setDimensions(DebugGraphComponent.DEFAULT_WIDTH, DebugGraphComponent.DEFAULT_HEIGHT));
            
        return graph;
    }
} 