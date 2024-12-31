package com.ur91k.jdiep.ecs.systems.gameplay;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.gameplay.FoodComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.HashMap;
import java.util.Random;

public class FoodDriftSystem extends IteratingSystem {
    private final World physicsWorld;
    private final Random random = new Random();
    private static final float DRIFT_FORCE = 12f;
    private static final float MAX_ANGULAR_VELOCITY = 0.6f;
    private static final float TORQUE_MAGNITUDE = 0.2f;
    private static final float NOISE_SCALE = 0.3f;
    private static final float DIRECTION_CHANGE_RATE = 0.1f;
    private static final float ANGULAR_CHANGE_RATE = 0.02f;  // How quickly to change rotation direction
    
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final HashMap<Entity, Float> targetAngles;  // Target angle for each entity
    private final HashMap<Entity, Float> targetAngularVels;  // Target angular velocity for each entity
    
    public FoodDriftSystem(World physicsWorld) {
        super(Family.all(FoodComponent.class, CollisionComponent.class).get());
        this.physicsWorld = physicsWorld;
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        this.targetAngles = new HashMap<>();
        this.targetAngularVels = new HashMap<>();
    }
    
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);  // Process every frame
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collision = collisionMapper.get(entity);
        Body body = collision.getBody();
        
        if (body != null) {
            // Initialize or get target angle for this entity
            Float targetAngle = targetAngles.computeIfAbsent(entity, 
                k -> random.nextFloat() * (float)(Math.PI * 2));
            
            // Initialize or update target angular velocity
            Float targetAngularVel = targetAngularVels.computeIfAbsent(entity,
                k -> (random.nextFloat() * 2 - 1) * MAX_ANGULAR_VELOCITY);
            
            // Occasionally change target angular velocity
            if (random.nextFloat() < ANGULAR_CHANGE_RATE) {
                targetAngularVel = (random.nextFloat() * 2 - 1) * MAX_ANGULAR_VELOCITY;
                targetAngularVels.put(entity, targetAngularVel);
            }
            
            // Smoothly change target angle
            targetAngle += (random.nextFloat() - 0.5f) * DIRECTION_CHANGE_RATE;
            targetAngles.put(entity, targetAngle);
            
            // Add some perlin-like noise to the movement
            float noiseOffsetX = (random.nextFloat() - 0.5f) * NOISE_SCALE;
            float noiseOffsetY = (random.nextFloat() - 0.5f) * NOISE_SCALE;
            
            // Calculate smooth force direction
            Vec2 force = new Vec2(
                (float)Math.cos(targetAngle) * DRIFT_FORCE + noiseOffsetX,
                (float)Math.sin(targetAngle) * DRIFT_FORCE + noiseOffsetY
            );
            
            // Scale force based on current velocity for natural movement
            Vec2 velocity = body.getLinearVelocity();
            float speed = velocity.length();
            float forceFactor = Math.max(0, 1 - speed / 3.0f);
            force.mulLocal(forceFactor * deltaTime);
            
            // Apply force at slightly offset positions for natural rotation
            Vec2 forcePoint = body.getWorldCenter().clone();
            forcePoint.addLocal(new Vec2(
                (random.nextFloat() - 0.5f) * 0.1f,
                (random.nextFloat() - 0.5f) * 0.1f
            ));
            
            body.applyForce(force, forcePoint);
            
            // Smooth angular velocity transition
            float currentAngularVel = body.getAngularVelocity();
            float angularDiff = targetAngularVel - currentAngularVel;
            float torque = angularDiff * TORQUE_MAGNITUDE;
            body.applyTorque(torque);
        }
    }
} 