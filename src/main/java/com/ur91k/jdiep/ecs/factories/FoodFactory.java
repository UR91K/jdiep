package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.ecs.components.gameplay.FoodComponent;
import com.ur91k.jdiep.ecs.components.gameplay.FoodType;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.physics.PhysicsProperties;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.game.config.GameConstants;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import org.tinylog.Logger;

public class FoodFactory {
    private final Engine engine;
    
    public FoodFactory(Engine engine) {
        this.engine = engine;
    }
    
    public Entity createTinyFood(Vector2f position) {
        Entity food = engine.createEntity();
        
        // Add transform
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        food.add(transform);
        
        // Add food component
        FoodComponent foodComp = engine.createComponent(FoodComponent.class);
        foodComp.init(10, FoodType.TINY);  // 10 XP for tiny food
        food.add(foodComp);
        
        // Add collision (square shape)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float size = GameConstants.TINY_FOOD_SIZE;
        Vector2f[] vertices = new Vector2f[] {
            new Vector2f(-size/2, -size/2),
            new Vector2f(size/2, -size/2),
            new Vector2f(size/2, size/2),
            new Vector2f(-size/2, size/2)
        };
        collision.init(food, vertices, CollisionFilters.CATEGORY_FOOD, CollisionFilters.MASK_FOOD);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.5f);
        collision.setFriction(0.1f);
        collision.setRestitution(0.5f);
        collision.setLinearDamping(2.0f);
        collision.setAngularDamping(1.0f);
        food.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(size, size);  // Square shape
        food.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS - 1);  // Render below tanks
        food.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.T_FOOD_FILL);
        color.setOutline(RenderingConstants.T_FOOD_OUTLINE, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        food.add(color);
        
        engine.addEntity(food);
        Logger.debug("Created tiny food at position: {}", position);
        return food;
    }
    
    public Entity createSmallFood(Vector2f position) {
        Entity food = engine.createEntity();
        
        // Add transform
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        food.add(transform);
        
        // Add food component
        FoodComponent foodComp = engine.createComponent(FoodComponent.class);
        foodComp.init(25, FoodType.SMALL);  // 25 XP for small food
        food.add(foodComp);
        
        // Add collision (triangle shape)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float size = GameConstants.SMALL_FOOD_SIZE;
        float height = size * (float)Math.sqrt(3) / 2;  // Height of equilateral triangle
        Vector2f[] vertices = new Vector2f[] {
            new Vector2f(0, height/2),  // Top
            new Vector2f(-size/2, -height/2),  // Bottom left
            new Vector2f(size/2, -height/2)   // Bottom right
        };
        collision.init(food, vertices, CollisionFilters.CATEGORY_FOOD, CollisionFilters.MASK_FOOD);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.5f);
        collision.setFriction(0.1f);
        collision.setRestitution(0.5f);
        collision.setLinearDamping(2.0f);
        collision.setAngularDamping(1.0f);
        food.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(vertices);  // Triangle shape
        food.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS - 1);
        food.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.S_FOOD_FILL);
        color.setOutline(RenderingConstants.S_FOOD_OUTLINE, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        food.add(color);
        
        engine.addEntity(food);
        Logger.debug("Created small food at position: {}", position);
        return food;
    }
    
    public Entity createMediumFood(Vector2f position) {
        Entity food = engine.createEntity();
        
        // Add transform
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        food.add(transform);
        
        // Add food component
        FoodComponent foodComp = engine.createComponent(FoodComponent.class);
        foodComp.init(130, FoodType.MEDIUM);  // 130 XP for medium food
        food.add(foodComp);
        
        // Add collision (pentagon shape)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float sideLength = GameConstants.MEDIUM_FOOD_SIZE;
        Vector2f[] vertices = createPentagonVertices(sideLength);
        collision.init(food, vertices, CollisionFilters.CATEGORY_FOOD, CollisionFilters.MASK_FOOD);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.5f);
        collision.setFriction(0.1f);
        collision.setRestitution(0.5f);
        collision.setLinearDamping(2.0f);
        collision.setAngularDamping(1.0f);
        food.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(vertices);  // Pentagon shape
        food.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS - 1);
        food.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.M_FOOD_FILL);
        color.setOutline(RenderingConstants.M_FOOD_OUTLINE, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        food.add(color);
        
        engine.addEntity(food);
        Logger.debug("Created medium food at position: {}", position);
        return food;
    }
    
    public Entity createLargeFood(Vector2f position) {
        Entity food = engine.createEntity();
        
        // Add transform
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        food.add(transform);
        
        // Add food component
        FoodComponent foodComp = engine.createComponent(FoodComponent.class);
        foodComp.init(500, FoodType.LARGE);  // 500 XP for large food
        food.add(foodComp);
        
        // Add collision (large pentagon shape)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float sideLength = GameConstants.LARGE_FOOD_SIZE;
        Vector2f[] vertices = createPentagonVertices(sideLength);
        collision.init(food, vertices, CollisionFilters.CATEGORY_FOOD, CollisionFilters.MASK_FOOD);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.5f);
        collision.setFriction(0.1f);
        collision.setRestitution(0.5f);
        collision.setLinearDamping(2.0f);
        collision.setAngularDamping(1.0f);
        food.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(vertices);  // Large pentagon shape
        food.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS - 1);
        food.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.L_FOOD_FILL);
        color.setOutline(RenderingConstants.L_FOOD_OUTLINE, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        food.add(color);
        
        engine.addEntity(food);
        Logger.debug("Created large food at position: {}", position);
        return food;
    }
    
    private Vector2f[] createPentagonVertices(float sideLength) {
        // Calculate radius from side length for a regular pentagon
        float radius = sideLength / (2 * (float)Math.sin(Math.PI / 5));
        
        Vector2f[] vertices = new Vector2f[5];
        for (int i = 0; i < 5; i++) {
            float angle = (float) (2 * Math.PI * i / 5 - Math.PI / 2);  // Start at top point
            vertices[i] = new Vector2f(
                (float) (radius * Math.cos(angle)),
                (float) (radius * Math.sin(angle))
            );
        }
        return vertices;
    }
} 