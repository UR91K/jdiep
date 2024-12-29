package com.ur91k.jdiep.ecs.systems.gameplay;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.gameplay.FoodComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import java.util.Random;

public class FoodDriftSystem extends IteratingSystem {
    private final World physicsWorld;
    private final Random random = new Random();
    private float driftTimer = 0;
    private static final float DRIFT_INTERVAL = 1.0f;  // Apply force every second
    private static final float DRIFT_FORCE = 10.0f;    // Base force magnitude
    
    private final ComponentMapper<CollisionComponent> collisionMapper;
    
    public FoodDriftSystem(World physicsWorld) {
        super(Family.all(FoodComponent.class, CollisionComponent.class).get());
        this.physicsWorld = physicsWorld;
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
    }
    
    @Override
    public void update(float deltaTime) {
        driftTimer += deltaTime;
        if (driftTimer >= DRIFT_INTERVAL) {
            super.update(deltaTime);
            driftTimer = 0;
        }
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collision = collisionMapper.get(entity);
        Body body = collision.getBody();
        
        if (body != null) {
            // Apply random drift force
            float angle = random.nextFloat() * (float)Math.PI * 2;
            Vec2 force = new Vec2(
                (float)Math.cos(angle) * DRIFT_FORCE,
                (float)Math.sin(angle) * DRIFT_FORCE
            );
            
            // Scale force based on current velocity to prevent excessive speed
            Vec2 velocity = body.getLinearVelocity();
            float speed = velocity.length();
            float forceFactor = Math.max(0, 1 - speed / 5.0f);  // Reduce force as speed increases
            force.mulLocal(forceFactor);
            
            body.applyForce(force, body.getWorldCenter());
        }
    }
} 