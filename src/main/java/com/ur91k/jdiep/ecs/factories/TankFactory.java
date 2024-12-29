package com.ur91k.jdiep.ecs.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import org.tinylog.Logger;
import com.ur91k.jdiep.ecs.components.camera.CameraTargetComponent;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerComponent;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankBodyComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TurretComponent;
import com.ur91k.jdiep.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.game.weapons.PhaseConfig;
import com.ur91k.jdiep.game.weapons.TurretPhase;
import com.ur91k.jdiep.graphics.config.RenderingConstants;
import com.ur91k.jdiep.graphics.core.RenderLayer;

import org.joml.Vector2f;

public class TankFactory {
    private final Engine engine;
    
    public TankFactory(Engine engine) {
        this.engine = engine;
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
        
        MovementComponent movement = engine.createComponent(MovementComponent.class);
        movement.init(200.0f);  // Default speed
        tank.add(movement);
        
        // Add rendering components
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(30.0f);  // Base tank radius
        tank.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS);  // Base layer for tank body
        tank.add(layer);
        
        ColorComponent bodyColor = engine.createComponent(ColorComponent.class);
        bodyColor.init(RenderingConstants.RED_FILL_COLOR);
        bodyColor.setOutline(RenderingConstants.RED_OUTLINE_COLOR, 4.0f);
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
        
        ShapeComponent shape = engine.createComponent(ShapeComponent.class);
        shape.init(turretWidth, turretHeight);
        turret.add(shape);
        
        RenderLayer layer = engine.createComponent(RenderLayer.class);
        layer.setLayer(RenderLayer.GAME_OBJECTS - 1);  // Render turret above tank body
        turret.add(layer);
        
        ColorComponent turretColor = engine.createComponent(ColorComponent.class);
        turretColor.init(RenderingConstants.TURRET_FILL_COLOR);
        turretColor.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, 4.0f);
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
            0.4f,                           // width ratio
            0.8f,                           // length ratio
            new Vector2f(0, 0),            // centered
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
        
        Logger.debug("Added player control components");
        return tank;
    }
} 