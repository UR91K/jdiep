package com.ur91k.jdiep.engine.ecs.entities;

import com.ur91k.jdiep.engine.graphics.RenderLayer;
import com.ur91k.jdiep.engine.graphics.RenderingConstants;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.ecs.World;
import com.ur91k.jdiep.engine.ecs.components.ColorComponent;
import com.ur91k.jdiep.engine.ecs.components.MouseAimComponent;
import com.ur91k.jdiep.engine.ecs.components.ParentComponent;
import com.ur91k.jdiep.engine.ecs.components.ShapeComponent;
import com.ur91k.jdiep.engine.ecs.components.TransformComponent;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.components.CameraComponent;
import com.ur91k.jdiep.engine.ecs.components.MovementComponent;

import org.joml.Vector2f;

public class EntityFactory {
    private static final Logger logger = Logger.getLogger(EntityFactory.class);
    private final World world;

    public EntityFactory(World world) {
        this.world = world;
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
        float tankRadius = 20.0f;  // Base tank radius
        float turretWidth = tankRadius * 0.8f;  // Turret width relative to tank size
        float turretLength = tankRadius * 1.8f;  // Turret length relative to tank size
        
        // Create tank body
        Entity tankBody = world.createEntity();
        
        // Add transform for the body - position is already in world coordinates
        tankBody.addComponent(new TransformComponent(
            position,
            new Vector2f(1, 1),
            0.0f
        ));

        // Add movement component with appropriate speed and friction
        tankBody.addComponent(new MovementComponent(300.0f, 5.0f));  // 300 units/sec speed, 5.0 friction

        // Add circle shape for the body
        tankBody.addComponent(new ShapeComponent(tankRadius));

        // Add color for the body
        ColorComponent bodyColor = new ColorComponent(RenderingConstants.RED_FILL_COLOR);
        bodyColor.setOutline(RenderingConstants.RED_OUTLINE_COLOR, 3.0f);
        tankBody.addComponent(bodyColor);

        // Add render layer for body (on top)
        tankBody.addComponent(new RenderLayer(RenderLayer.BODY));

        // Create turret
        Entity turret = world.createEntity();
        
        // Calculate optimal turret offset
        float turretOffset = calculateTurretOffset(tankRadius, turretWidth);
        
        // Calculate turret starting position (where it meets the circle)
        float startX = tankRadius - turretOffset;
        // Turret extends outward from the starting position
        float endX = startX + turretLength;
        // Center position is halfway between start and end
        float centerOffset = (startX + endX) / 2;
        
        // Add transform for the turret
        turret.addComponent(new TransformComponent(
            position,  // Initial position doesn't matter, will be updated by ParentSystem
            new Vector2f(turretLength/tankRadius, turretWidth/tankRadius),  // Scale relative to tank radius
            0.0f
        ));

        // Add rectangle shape for the turret
        turret.addComponent(new ShapeComponent(
            ShapeComponent.ShapeType.RECTANGLE,
            tankRadius/2,  // Base size (will be scaled by transform)
            4
        ));

        // Add color for the turret
        ColorComponent turretColor = new ColorComponent(RenderingConstants.TURRET_FILL_COLOR);
        turretColor.setOutline(RenderingConstants.TURRET_OUTLINE_COLOR, 3.0f);
        turret.addComponent(turretColor);

        // Add render layer for turret (behind body)
        turret.addComponent(new RenderLayer(RenderLayer.TURRET));

        // Add parent component to link turret to body
        turret.addComponent(new ParentComponent(
            tankBody,
            new Vector2f(centerOffset, 0.0f),  // Position turret center at the calculated offset
            0.0f  // No initial rotation relative to parent
        ));

        // Add mouse aim component to tank body, targeting the turret
        tankBody.addComponent(new MouseAimComponent(turret));

        // Debug output
        logger.debug("Turret dimensions - Start X: {}, End X: {}, Center offset: {}", 
                    startX, endX, centerOffset);

        return tankBody;  // Return the body entity (parent)
    }

    public Entity createCamera(Vector2f position) {
        Entity camera = world.createEntity();
        camera.addComponent(new TransformComponent(
            position,
            new Vector2f(1, 1),  // No scale for camera
            0.0f                 // No rotation for camera
        ));
        camera.addComponent(new CameraComponent());
        logger.debug("Created camera entity at world position: {}", position);
        return camera;
    }
} 