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
    
    // Create the tank body entity
    private Entity createTankBody(float mass, int phaseCount, float reloadTime, Vector2f position) {
        Entity tank = world.createEntity();
        
        // Add core components
        tank.addComponent(new TransformComponent());
        tank.addComponent(new TankBodyComponent(mass, phaseCount, reloadTime));
        tank.addComponent(new MovementComponent(200.0f, 0.9f));
        
        // Add rendering components
        tank.addComponent(new ShapeComponent(30.0f)); // Base tank radius
        tank.addComponent(new RenderLayer(RenderLayer.BODY));
        ColorComponent bodyColor = new ColorComponent(RenderingConstants.RED_FILL_COLOR);
        bodyColor.setOutline(RenderingConstants.RED_OUTLINE_COLOR, 3.0f);
        tank.addComponent(bodyColor);
        
        // Set position
        tank.getComponent(TransformComponent.class).setPosition(position);
        
        return tank;
    }
    
    // Create a turret entity
    private Entity createTurret(Entity tankBody, float widthRatio, float lengthRatio, 
                              Vector2f offset, float rotation, TurretPhase phase) {
        Entity turret = world.createEntity();
        logger.debug("Creating turret entity {}", turret.getId());
        
        // Add turret component
        TurretComponent turretComp = new TurretComponent(
            widthRatio, lengthRatio,
            offset, rotation, phase
        );
        turret.addComponent(turretComp);
        
        // Add transform (will be updated by parent system)
        turret.addComponent(new TransformComponent());
        
        // Add parent relationship
        ParentComponent parentComp = new ParentComponent(tankBody);
        parentComp.setLocalOffset(offset);
        parentComp.setLocalRotation(rotation);
        turret.addComponent(parentComp);
        
        // Add rendering components
        float turretWidth = 30.0f * widthRatio;
        float turretLength = 30.0f * lengthRatio;
        ShapeComponent shape = new ShapeComponent(turretWidth, turretLength);
        turret.addComponent(shape);
        
        turret.addComponent(new RenderLayer(RenderLayer.TURRET));
        ColorComponent turretColor = new ColorComponent(RenderingConstants.TURRET_FILL_COLOR);
        turretColor.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, 2.0f);
        turret.addComponent(turretColor);
        
        logger.debug("Turret components: Shape={}, Color={}, RenderLayer={}, Transform={}, Parent={}",
            turret.hasComponent(ShapeComponent.class),
            turret.hasComponent(ColorComponent.class),
            turret.hasComponent(RenderLayer.class),
            turret.hasComponent(TransformComponent.class),
            turret.hasComponent(ParentComponent.class)
        );
        
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
        logger.debug("Creating twin tank at position: {}", position);
        
        Entity tankBody = createTankBody(120, 2, 1.2f, position);
        TankBodyComponent body = tankBody.getComponent(TankBodyComponent.class);
        PhaseConfig phaseConfig = body.getPhaseConfig();
        
        // Create twin turrets
        createTurret(
            tankBody,
            0.3f, 0.7f,
            new Vector2f(-0.2f, 0),
            0.0f,
            new TurretPhase(phaseConfig, 1)
        );
        
        createTurret(
            tankBody,
            0.3f, 0.7f,
            new Vector2f(0.2f, 0),
            0.0f,
            new TurretPhase(phaseConfig, 2)
        );
        
        logger.debug("Twin tank created");
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
        logger.debug("Making tank player controlled");
        tank.addComponent(new PlayerComponent("local", true, "Player"));
        tank.addComponent(new PlayerControlledComponent());
        tank.addComponent(new CameraTargetComponent());
        
        logger.debug("Added player control components");
        return tank;
    }

    protected float calculateTurretOffset(float radius, float width) {
        // Prevent invalid math if turret width is greater than diameter
        if (width > 2 * radius) {
            logger.warn("Turret width ({}) is greater than tank diameter ({})", width, 2*radius);
            width = 2 * radius;
        }

        // Use Pythagorean theorem to calculate offset where turret meets circle
        float offset = radius - (float)Math.sqrt(radius * radius - (width/2)*(width/2));

        // Debug output
        logger.debug("Tank radius: {}", radius);
        logger.debug("Turret width: {}", width);
        logger.debug("Calculated offset: {}", offset);

        return offset;
    }
} 