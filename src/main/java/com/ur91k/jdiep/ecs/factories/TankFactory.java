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
import com.ur91k.jdiep.ecs.components.gameplay.TurretComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.physics.TurretJointComponent;
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
                    CollisionComponent collision = tank.getComponent(CollisionComponent.class);
                    
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
    
    private Entity createTurret(Entity tankBody, Vector2f mountPoint, float mountAngleOffset, 
                              float width, float length, float reloadTime, float recoilForce, int phase) {
        Entity turret = engine.createEntity();
        Logger.debug("Creating physics-based turret for tank");
        
        // Add transform component (will be updated by physics)
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(mountPoint);  // Initial position at mount point
        transform.setRotation(mountAngleOffset - (float)Math.PI/2);  // Subtract 90 degrees for clockwise rotation
        turret.add(transform);
        
        // Add turret component
        TurretComponent turretComp = engine.createComponent(TurretComponent.class);
        turretComp.init(tankBody, width, length, reloadTime, recoilForce, phase);
        turret.add(turretComp);
        
        // Add joint component
        TurretJointComponent jointComp = engine.createComponent(TurretJointComponent.class);
        jointComp.init(
            mountPoint,
            mountAngleOffset - (float)Math.PI/2,  // Subtract 90 degrees for clockwise rotation
            2.0f,           // Motor speed (rad/s)
            1000.0f,        // Max motor torque
            -3.14159f,      // Lower angle limit (full rotation)
            3.14159f        // Upper angle limit
        );
        turret.add(jointComp);
        
        // Add collision component (rectangle shape for turret)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        // Move origin to base of turret and rotate 90 degrees
        Vector2f[] vertices = new Vector2f[] {
            new Vector2f(-width/2, 0),          // Bottom left (at mount point)
            new Vector2f(width/2, 0),           // Bottom right (at mount point)
            new Vector2f(width/2, length),      // Top right
            new Vector2f(-width/2, length)      // Top left
        };
        collision.init(turret, vertices, CollisionFilters.CATEGORY_TURRET, CollisionFilters.MASK_TURRET);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(0.15f);      // Very light, like balsa wood (tank body is 1.0)
        collision.setFriction(0.1f);      // Keep low friction
        collision.setRestitution(0.2f);   // Keep some bounce
        collision.setLinearDamping(0.5f); // Keep some damping
        collision.setAngularDamping(1.0f);// Reduce angular damping for more responsive rotation
        turret.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(vertices);
        turret.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS + 1);  // Render above tank
        turret.add(layer);
        
        ColorComponent color = engine.createComponent(ColorComponent.class);
        color.init(RenderingConstants.TURRET_FILL_COLOR);
        color.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        turret.add(color);
        
        engine.addEntity(turret);
        return turret;
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
        
        // Add controller component for input handling
        TankControllerComponent controller = engine.createComponent(TankControllerComponent.class);
        tank.add(controller);
        
        // Add collision component with Box2D-friendly values
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float radius = body.getRadius();  // Use TankScaling radius based on mass
        collision.init(tank, radius, CollisionFilters.CATEGORY_TANK, CollisionFilters.MASK_TANK);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(1.0f);       // Standard density for mass calculation
        collision.setFriction(0.2f);      // Low friction for smooth movement
        collision.setRestitution(0.2f);   // Slight bounce
        collision.setLinearDamping(1.0f); // Damping for smooth movement
        collision.setAngularDamping(4.0f);// Higher angular damping for better control
        collision.setBullet(true);        // Enable continuous collision detection
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
        // Create tank body
        Entity tankBody = createTankBody(100, 1, 1.0f, position);
        TankBodyComponent body = tankBody.getComponent(TankBodyComponent.class);
        
        // Create single centered turret
        float turretWidth = body.getRadius() * 0.6f;   // 60% of tank radius
        float turretLength = body.getRadius() * 2.0f;  // 2x tank radius
        createTurret(
            tankBody,
            position,                 // Mount at tank position
            0.0f,                    // No angle offset
            turretWidth,
            turretLength,
            1.0f,                    // 1 second reload time
            100.0f,                  // Recoil force
            1                        // Single phase
        );
        
        return tankBody;
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
} 