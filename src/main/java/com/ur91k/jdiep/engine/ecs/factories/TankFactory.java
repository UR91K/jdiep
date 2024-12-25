package com.ur91k.jdiep.engine.ecs.factories;

import com.ur91k.jdiep.engine.ecs.components.camera.CameraTargetComponent;
import com.ur91k.jdiep.engine.ecs.components.gameplay.PlayerComponent;
import com.ur91k.jdiep.engine.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.engine.ecs.components.gameplay.TankBodyComponent;
import com.ur91k.jdiep.engine.ecs.components.gameplay.TurretComponent;
import com.ur91k.jdiep.engine.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.engine.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.engine.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.engine.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.engine.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.engine.core.logging.Logger;
import com.ur91k.jdiep.engine.debug.components.DebugStateComponent;
import com.ur91k.jdiep.engine.graphics.config.RenderingConstants;
import com.ur91k.jdiep.engine.graphics.core.RenderLayer;
import com.ur91k.jdiep.game.weapons.PhaseConfig;
import com.ur91k.jdiep.game.weapons.TurretPhase;

import org.joml.Vector2f;

import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.World;

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
        bodyColor.setOutline(RenderingConstants.RED_OUTLINE_COLOR, 4.0f);
        tank.addComponent(bodyColor);
        
        // Set position
        tank.getComponent(TransformComponent.class).setPosition(position);
        
        return tank;
    }
    
    // Create a turret entity
    private Entity createTurret(Entity tankBody, float widthRatio, float lengthRatio, 
                              Vector2f offset, float rotation, TurretPhase phase) {
        Entity turret = world.createEntity();
        logger.debug("Creating turret entity {} for tank {}", turret.getId(), tankBody.getId());
        
        // Add turret component
        TurretComponent turretComp = new TurretComponent(
            widthRatio, lengthRatio,
            offset, rotation, phase
        );
        turret.addComponent(turretComp);
        
        // Add transform (will be updated by parent system)
        TransformComponent transform = new TransformComponent();
        transform.setPosition(tankBody.getComponent(TransformComponent.class).getPosition());
        transform.setRotation((float)Math.PI / 2); // Add 90-degree rotation to make length point right
        turret.addComponent(transform);
        
        // Add parent relationship
        ParentComponent parentComp = new ParentComponent(tankBody);
        parentComp.setLocalOffset(offset);
        parentComp.setLocalRotation(rotation);
        turret.addComponent(parentComp);
        
        // Add rendering components
        float tankRadius = tankBody.getComponent(TankBodyComponent.class).getRadius();
        // Swap width and length for correct orientation
        float turretWidth = tankRadius * 2 * lengthRatio;  // Use length for width
        float turretHeight = tankRadius * 2 * widthRatio;  // Use width for height
        ShapeComponent shape = new ShapeComponent(turretWidth, turretHeight);
        turret.addComponent(shape);
        
        turret.addComponent(new RenderLayer(RenderLayer.TURRET));
        ColorComponent turretColor = new ColorComponent(RenderingConstants.TURRET_FILL_COLOR);
        turretColor.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, 4.0f);
        turret.addComponent(turretColor);
        
        logger.debug("Turret {} components:", turret.getId());
        logger.debug("- Shape: type={}, width={}, height={}", 
            shape.getType(), shape.getWidth(), shape.getHeight());
        logger.debug("- Transform: pos={}, rot={}", 
            transform.getPosition(), transform.getRotation());
        logger.debug("- Parent: offset={}, rot={}", 
            parentComp.getLocalOffset(), parentComp.getLocalRotation());
        logger.debug("- Color: fill={}, outline={}", 
            turretColor.getFillColor(), turretColor.getOutlineColor());
        
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
            new Vector2f(0.2f * tankRadius, -15.0f),  // Right turret
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

    public Entity createTank(Vector2f position) {
        Entity tank = world.createEntity();
        
        // Add existing components
        TransformComponent transform = new TransformComponent();
        transform.setPosition(position);
        tank.addComponent(transform);
        
        MovementComponent movement = new MovementComponent();
        tank.addComponent(movement);
        
        // Add debug component
        DebugStateComponent debug = new DebugStateComponent();
        debug.setValue("type", "Tank");
        debug.setValue("created_at", System.currentTimeMillis());
        tank.addComponent(debug);
        
        // ... rest of existing tank setup ...
        return tank;
    }
} 