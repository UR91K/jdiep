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
import com.ur91k.jdiep.ecs.components.physics.VelocityComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionFilters;
import com.ur91k.jdiep.ecs.components.physics.PhysicsProperties;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.game.weapons.PhaseConfig;
import com.ur91k.jdiep.game.weapons.TurretPhase;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;
import com.ur91k.jdiep.debug.ImGuiDebugManager;

public class TankFactory {
    private final Engine engine;
    private final ImGuiDebugManager debugManager;
    
    public TankFactory(Engine engine, ImGuiDebugManager debugManager) {
        this.engine = engine;
        this.debugManager = debugManager;
        
        // Set initial physics values in debug window
        debugManager.setTankPhysicsValues(800.0f, 0.05f, 0.2f, 3.0f, 1.0f, 0.5f);
        
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
        
        // Add core components using pooling
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(position);
        tank.add(transform);
        
        TankBodyComponent body = engine.createComponent(TankBodyComponent.class);
        body.init(mass, phaseCount, reloadTime);
        tank.add(body);
        
        // Add velocity component for movement
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        velocity.setAcceleration(800.0f);  // Default acceleration
        tank.add(velocity);
        
        // Add controller component
        tank.add(engine.createComponent(TankControllerComponent.class));
        
        // Add collision component
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        float radius = RenderingConstants.DEFAULT_TANK_RADIUS;  // Use standard tank size
        collision.init(tank, radius, CollisionFilters.CATEGORY_TANK, CollisionFilters.MASK_TANK);
        collision.setBodyType(BodyType.DYNAMIC);
        collision.setDensity(1.0f);  // Normal density
        collision.setFriction(0.05f);  // Reduced friction for smoother movement
        collision.setRestitution(0.5f);  // Low bounce
        collision.setLinearDamping(0.2f);  // Significantly reduced damping
        collision.setAngularDamping(3.0f);  // Keep high angular damping to prevent spinning
        tank.add(collision);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(radius);  // Use same radius as collision
        tank.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS);  // Base layer for tank body
        tank.add(layer);
        
        ColorComponent bodyColor = engine.createComponent(ColorComponent.class);
        bodyColor.init(RenderingConstants.RED_FILL_COLOR);
        bodyColor.setOutline(RenderingConstants.RED_OUTLINE_COLOR, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        tank.add(bodyColor);
        
        engine.addEntity(tank);
        return tank;
    }
    
    // Create a turret entity
    private Entity createTurret(Entity tankBody, float widthRatio, float lengthRatio, 
                              Vector2f offset, float rotation, TurretPhase phase) {
        Entity turret = engine.createEntity();
        Logger.debug("Creating turret entity for tank");
        
        // Add turret component using pooling
        TurretComponent turretComp = engine.createComponent(TurretComponent.class);
        turretComp.init(widthRatio, lengthRatio, offset, rotation, phase);
        turret.add(turretComp);
        
        // Add transform (will be updated by parent system)
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        transform.setPosition(new Vector2f(0, 0));  // Will be updated by ParentSystem
        transform.setRotation((float)Math.PI / 2); // Add 90-degree rotation to make length point right
        turret.add(transform);
        
        // Add parent relationship
        ParentComponent parentComp = engine.createComponent(ParentComponent.class);
        parentComp.init(tankBody, offset, rotation);
        turret.add(parentComp);
        
        // Add rendering components
        TankBodyComponent tankBodyComp = tankBody.getComponent(TankBodyComponent.class);
        float tankRadius = tankBodyComp.getRadius();
        
        // Swap width and length for correct orientation
        float turretWidth = tankRadius * 2 * lengthRatio;  // Use length for width
        float turretHeight = tankRadius * 2 * widthRatio;  // Use width for height
        
        // Add collision component (kinematic - moved by code)
        CollisionComponent collision = engine.createComponent(CollisionComponent.class);
        Vector2f[] vertices = new Vector2f[] {
            new Vector2f(-turretWidth/2, -turretHeight/2),
            new Vector2f(turretWidth/2, -turretHeight/2),
            new Vector2f(turretWidth/2, turretHeight/2),
            new Vector2f(-turretWidth/2, turretHeight/2)
        };
        collision.init(turret, vertices, CollisionFilters.CATEGORY_TURRET, CollisionFilters.MASK_TURRET);
        collision.setBodyType(BodyType.KINEMATIC);
        collision.setDensity(PhysicsProperties.TURRET_DENSITY);
        collision.setFriction(PhysicsProperties.TURRET_FRICTION);
        collision.setRestitution(PhysicsProperties.TURRET_RESTITUTION);
        turret.add(collision);
        
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(turretWidth, turretHeight);
        turret.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS - 1);  // Render turret above tank body
        turret.add(layer);
        
        ColorComponent turretColor = engine.createComponent(ColorComponent.class);
        turretColor.init(RenderingConstants.TURRET_FILL_COLOR);
        turretColor.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, RenderingConstants.DEFAULT_OUTLINE_WIDTH);
        turret.add(turretColor);
        
        engine.addEntity(turret);
        return turret;
    }
    
    // Tank class presets
    public Entity createBasicTank(Vector2f position) {
        Entity tankBody = createTankBody(100, 1, 1.0f, position);
        TankBodyComponent body = tankBody.getComponent(TankBodyComponent.class);
        
        // Create single forward-facing turret
        createTurret(
            tankBody,
            0.067f,
            0.20f,
            new Vector2f(20, 0),            // centered
            0.0f,                          // forward facing
            new TurretPhase(body.getPhaseConfig(), 1) // single phase
        );
        
        return tankBody;
    }
    
    public Entity createTwinTank(Vector2f position) {
        Logger.debug("Creating twin tank at position: {}", position);
        
        Entity tankBody = createTankBody(120, 2, 1.2f, position);
        TankBodyComponent body = tankBody.getComponent(TankBodyComponent.class);
        PhaseConfig phaseConfig = body.getPhaseConfig();
        float tankRadius = body.getRadius();
        
        // Create twin turrets with offsets relative to tank radius
        createTurret(
            tankBody,
            0.067f, 0.14f,
            new Vector2f(0.2f * tankRadius, 15.0f),  // Right turret
            0.0f,
            new TurretPhase(phaseConfig, 2)
        );

        createTurret(
            tankBody,
            0.067f, 0.14f,
            new Vector2f(0.2f * tankRadius, -15.0f),  // Left turret
            0.0f,
            new TurretPhase(phaseConfig, 2)
        );
        
        return tankBody;
    }
    
    public Entity createFlankGuard(Vector2f position) {
        Entity tankBody = createTankBody(110, 2, 1.5f, position);
        TankBodyComponent body = tankBody.getComponent(TankBodyComponent.class);
        PhaseConfig phaseConfig = body.getPhaseConfig();
        
        // Create front and back turrets
        createTurret(
            tankBody,
            0.4f, 0.8f,
            new Vector2f(0, 0),
            0.0f,
            new TurretPhase(phaseConfig, 1)
        );
        
        createTurret(
            tankBody,
            0.3f, 0.6f,
            new Vector2f(0, 0),
            (float)Math.PI,  // 180 degrees
            new TurretPhase(phaseConfig, 2)
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
        
        // Update turret render layers too
        for (Entity turret : engine.getEntitiesFor(Family.all(TurretComponent.class, ParentComponent.class).get())) {
            ParentComponent parentComp = turret.getComponent(ParentComponent.class);
            if (parentComp.getParent() == tank) {
                RenderLayer turretLayer = turret.getComponent(RenderLayer.class);
                turretLayer.setLayer(RenderLayer.GAME_OBJECTS + 99);  // Turrets render above tank
            }
        }
        
        Logger.debug("Added player control components and updated render layers");
        return tank;
    }
} 