package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import org.tinylog.Logger;
import com.ur91k.jdiep.ecs.components.camera.CameraTargetComponent;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerComponent;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankBodyComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankControllerComponent;
import com.ur91k.jdiep.ecs.components.physics.VelocityComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.physics.PhysicsProperties;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import com.ur91k.jdiep.debug.ImGuiDebugManager;
import com.ur91k.jdiep.game.config.GameUnits;

public class TankFactory {
    private final Engine engine;
    private final ImGuiDebugManager debugManager;
    
    public TankFactory(Engine engine, ImGuiDebugManager debugManager) {
        this.engine = engine;
        this.debugManager = debugManager;
        
        // Set initial physics values in debug window
        debugManager.setTankPhysicsValues(
            12.0f,    // acceleration (m/s²)
            0.2f,    // friction
            1.0f,    // linear damping
            4.0f,    // angular damping
            1.0f,    // density (kg/m²)
            0.2f     // restitution
        );
        
        // Register callback for physics value updates
        debugManager.setTankPhysicsCallback(new ImGuiDebugManager.TankPhysicsCallback() {
            @Override
            public void onTankPhysicsUpdate(float acceleration, float friction,
                                          float linearDamping, float angularDamping, float density,
                                          float restitution, float velocityFriction) {
                // Update all existing tanks
                for (Entity tank : engine.getEntitiesFor(Family.all(TankBodyComponent.class).get())) {
                    VelocityComponent velocity = tank.getComponent(VelocityComponent.class);
                    CollisionComponent collision = tank.getComponent(CollisionComponent.class);
                    
                    if (velocity != null) {
                        velocity.setAcceleration(acceleration);
                        velocity.setFriction(velocityFriction);
                    }
                    
                    if (collision != null) {
                        collision.setFriction(friction);
                        collision.setLinearDamping(linearDamping);
                        collision.setAngularDamping(angularDamping);
                        collision.setDensity(density);
                        collision.setRestitution(restitution);
                    }
                }
            }
        });
    }
    
    // Create the tank body entity
    private Entity createTankBody(float mass, int phaseCount, float reloadTime, Vector2f position) {
        Entity tank = engine.createEntity();
        Logger.debug("Creating tank body entity");
        
        // Add transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        tank.add(transform);
        
        // Add tank body component
        TankBodyComponent body = engine.createComponent(TankBodyComponent.class);
        body.init(mass, phaseCount, reloadTime);
        tank.add(body);
        
        // Add velocity component for movement
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        velocity.setAcceleration(12.0f);   // 4 m/s² - gentler acceleration
        velocity.setMaxSpeed(3.0f);       // 3 m/s max speed
        velocity.setFriction(0.95f);      // Keep friction as is since it's a multiplier
        tank.add(velocity);
        
        // Add controller component
        TankControllerComponent controller = engine.createComponent(TankControllerComponent.class);
        tank.add(controller);
        
        // Add collision component with Box2D-friendly values
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float radius = body.getRadius();  // Use TankScaling radius based on mass
        collision.init(tank, radius, CollisionFilters.CATEGORY_TANK, CollisionFilters.MASK_TANK);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(1.0f);       // Keep standard density
        collision.setFriction(0.2f);      // Slightly more friction for better control
        collision.setRestitution(0.2f);   // Keep slight bounce
        collision.setLinearDamping(1.0f); // Add linear damping for smoother stops
        collision.setAngularDamping(4.0f);// Higher angular damping for better turning control
        tank.add(collision);
        
        // Add shape component for rendering
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(radius);  // Use same radius as collision
        tank.add(shape);
        
        // Add color component
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.RED_FILL_COLOR);
        color.setOutline(RenderingConstants.RED_OUTLINE_COLOR, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        tank.add(color);
        
        // Add render layer
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS);
        tank.add(layer);
        
        engine.addEntity(tank);
        return tank;
    }
    
    // Tank class presets
    public Entity createBasicTank(Vector2f position) {
        return createTankBody(100, 1, 1.0f, position);
    }
    
    public Entity createTwin(Vector2f position) {
        return createTankBody(120, 2, 1.2f, position);
    }
    
    public Entity createFlankGuard(Vector2f position) {
        return createTankBody(110, 2, 1.5f, position);
    }
    
    // Create player-controlled version of any tank
    public Entity makePlayerControlled(Entity tank) {
        Logger.debug("Making tank player controlled");
        
        PlayerComponent playerComp = engine.createComponent(PlayerComponent.class);
        playerComp.init("local", true, "Player");
        tank.add(playerComp);
        
        tank.add(engine.createComponent(PlayerControlledComponent.class));
        tank.add(engine.createComponent(CameraTargetComponent.class));
        
        // Set higher render layer for player tank
        RenderLayer tankLayer = tank.getComponent(RenderLayer.class);
        tankLayer.setLayer(RenderLayer.GAME_OBJECTS + 100);  // Player tank renders above all other tanks
        
        Logger.debug("Added player control components and updated render layers");
        return tank;
    }
    
    public Entity createTank() {
        Entity tank = engine.createEntity();
        
        // Transform component
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        tank.add(transform);
        
        // Shape component for tank body
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(GameUnits.Tank.BODY_RADIUS);  // Initialize as circle with radius
        tank.add(shape);
        
        // Velocity component with Box2D-friendly values
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        velocity.setMaxSpeed(10.0f);  // 10 m/s is a reasonable Box2D speed
        velocity.setAcceleration(30.0f);  // Good acceleration for Box2D
        velocity.setFriction(0.95f);  // Keep friction as is since it's a multiplier
        tank.add(velocity);
        
        // Collision component with Box2D-friendly values
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        collision.setFriction(0.1f);  // Standard Box2D friction
        collision.setLinearDamping(0.5f);  // Moderate damping
        collision.setAngularDamping(3.0f);  // Higher angular damping for better control
        collision.setDensity(1.0f);  // Standard density of 1 kg/m²
        collision.setRestitution(0.2f);  // Slight bounce
        tank.add(collision);
        
        // Tank controller component
        TankControllerComponent controller = engine.createComponent(TankControllerComponent.class);
        tank.add(controller);
        
        return tank;
    }
} 