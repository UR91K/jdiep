package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.game.config.GameConstants;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.tinylog.Logger;

public class WorldBoundsFactory {
    private final Engine engine;
    private static final float WALL_THICKNESS = 10.0f;  // Thickness of boundary walls
    private static final float HIGH_FRICTION = 0.8f;    // High friction to slow things down at boundaries

    public WorldBoundsFactory(Engine engine) {
        this.engine = engine;
    }

    public void createWorldBounds() {
        float bounds = GameConstants.WORLD_BOUNDS;
        
        // Create four walls
        createWall(new Vector2f(-bounds, 0), new Vector2f(WALL_THICKNESS, bounds * 2));  // Left wall
        createWall(new Vector2f(bounds, 0), new Vector2f(WALL_THICKNESS, bounds * 2));   // Right wall
        createWall(new Vector2f(0, -bounds), new Vector2f(bounds * 2, WALL_THICKNESS));  // Bottom wall
        createWall(new Vector2f(0, bounds), new Vector2f(bounds * 2, WALL_THICKNESS));   // Top wall
        
        Logger.info("Created world boundary walls at size: {}", bounds);
    }

    private void createWall(Vector2f position, Vector2f size) {
        Entity wall = engine.createEntity();

        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        wall.add(transform);

        // Add collision component with rectangle shape
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        Vector2f[] vertices = createRectangleVertices(size);
        collision.init(wall, vertices, CollisionFilters.CATEGORY_WALL, CollisionFilters.MASK_WALL);
        collision.setBodyType(BodyType.STATIC);  // Walls don't move
        collision.setFriction(HIGH_FRICTION);    // High friction to slow things down
        collision.setRestitution(0.2f);         // Some bounce, but not too much
        wall.add(collision);

        engine.addEntity(wall);
    }

    private Vector2f[] createRectangleVertices(Vector2f size) {
        float halfWidth = size.x / 2;
        float halfHeight = size.y / 2;
        return new Vector2f[] {
            new Vector2f(-halfWidth, -halfHeight),  // Bottom left
            new Vector2f(halfWidth, -halfHeight),   // Bottom right
            new Vector2f(halfWidth, halfHeight),    // Top right
            new Vector2f(-halfWidth, halfHeight)    // Top left
        };
    }
} 