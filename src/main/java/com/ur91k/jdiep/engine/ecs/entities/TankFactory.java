package com.ur91k.jdiep.engine.ecs.entities;

import com.ur91k.jdiep.engine.ecs.components.*;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.firing.*;
import com.ur91k.jdiep.engine.ecs.World;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.graphics.RenderLayer;
import com.ur91k.jdiep.engine.graphics.RenderingConstants;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class TankFactory {
    private static final Logger logger = Logger.getLogger(TankFactory.class);
    private final World world;
    
    public TankFactory(World world) {
        this.world = world;
    }
    
    // Base method for all tanks
    private Entity createBaseTank(float mass, int phaseCount, float reloadTime) {
        Entity tank = world.createEntity();
        tank.addComponent(new TransformComponent());
        tank.addComponent(new TankBodyComponent(mass, phaseCount, reloadTime));
        tank.addComponent(new MovementComponent(200.0f, 0.9f));
        
        // Add body rendering components
        tank.addComponent(new ShapeComponent(30.0f)); // Base tank radius
        tank.addComponent(new RenderLayer(RenderLayer.BODY));
        ColorComponent bodyColor = new ColorComponent(RenderingConstants.RED_FILL_COLOR);
        bodyColor.setOutline(RenderingConstants.RED_OUTLINE_COLOR, 3.0f);
        tank.addComponent(bodyColor);
        
        return tank;
    }
    
    // Helper method to create turret rendering components
    private void addTurretRendering(Entity tank, TurretComponent turret) {
        ColorComponent turretColor = new ColorComponent(RenderingConstants.TURRET_FILL_COLOR);
        turretColor.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, 2.0f);
        tank.addComponent(turretColor);
        tank.addComponent(new RenderLayer(RenderLayer.TURRET));
    }
    
    // Tank class presets
    public Entity createBasicTank(Vector2f position) {
        Entity tank = createBaseTank(100, 1, 1.0f);
        TransformComponent transform = tank.getComponent(TransformComponent.class);
        transform.setPosition(position);
        
        TankBodyComponent body = tank.getComponent(TankBodyComponent.class);
        PhaseConfig phaseConfig = body.getPhaseConfig();
        
        // Add single forward-facing turret
        TurretComponent turret = new TurretComponent(
            0.4f,                           // width ratio
            0.8f,                           // length ratio
            new Vector2f(0, 0),            // centered
            0.0f,                          // forward facing
            new TurretPhase(phaseConfig, 1) // single phase
        );
        tank.addComponent(turret);
        addTurretRendering(tank, turret);
        
        return tank;
    }
    
    public Entity createTwinTank(Vector2f position) {
        logger.debug("Creating twin tank at position: {}", position);
        
        Entity tank = createBaseTank(120, 2, 1.2f);
        TransformComponent transform = tank.getComponent(TransformComponent.class);
        transform.setPosition(position);
        
        TankBodyComponent body = tank.getComponent(TankBodyComponent.class);
        PhaseConfig phaseConfig = body.getPhaseConfig();
        
        // Add twin turrets
        TurretComponent leftTurret = new TurretComponent(
            0.3f, 0.7f, 
            new Vector2f(-0.2f, 0), 
            0.0f,
            new TurretPhase(phaseConfig, 1)
        );
        tank.addComponent(leftTurret);
        addTurretRendering(tank, leftTurret);
        
        TurretComponent rightTurret = new TurretComponent(
            0.3f, 0.7f, 
            new Vector2f(0.2f, 0), 
            0.0f,
            new TurretPhase(phaseConfig, 2)
        );
        tank.addComponent(rightTurret);
        addTurretRendering(tank, rightTurret);
        
        logger.debug("Twin tank created with {} components", tank.getComponents().size());
        return tank;
    }
    
    public Entity createFlankGuard(Vector2f position) {
        Entity tank = createBaseTank(110, 2, 1.5f);
        TransformComponent transform = tank.getComponent(TransformComponent.class);
        transform.setPosition(position);
        
        TankBodyComponent body = tank.getComponent(TankBodyComponent.class);
        PhaseConfig phaseConfig = body.getPhaseConfig();
        
        // Add front and back turrets
        TurretComponent frontTurret = new TurretComponent(
            0.4f, 0.8f,
            new Vector2f(0, 0),
            0.0f,
            new TurretPhase(phaseConfig, 1)
        );
        tank.addComponent(frontTurret);
        addTurretRendering(tank, frontTurret);
        
        TurretComponent backTurret = new TurretComponent(
            0.3f, 0.6f,
            new Vector2f(0, 0),
            (float)Math.PI,  // 180 degrees
            new TurretPhase(phaseConfig, 2)
        );
        tank.addComponent(backTurret);
        addTurretRendering(tank, backTurret);
        
        return tank;
    }
    
    // Create player-controlled version of any tank
    public Entity makePlayerControlled(Entity tank) {
        logger.debug("Making tank player controlled");
        tank.addComponent(new PlayerComponent("local", true, "Player"));
        tank.addComponent(new PlayerControlledComponent());
        tank.addComponent(new CameraTargetComponent());
        
        logger.debug("Added player control components, total components: {}", tank.getComponents().size());
        return tank;
    }
} 