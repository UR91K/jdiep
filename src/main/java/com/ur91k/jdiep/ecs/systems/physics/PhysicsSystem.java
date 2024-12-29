package com.ur91k.jdiep.ecs.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class PhysicsSystem extends IteratingSystem implements ContactListener {
    private static final float TIME_STEP = 1.0f / 60.0f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    
    private final World world;
    private float accumulator;
    
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    
    public PhysicsSystem() {
        super(Family.all(TransformComponent.class, CollisionComponent.class).get());
        
        // Create Box2D world with gravity
        Vec2 gravity = new Vec2(0.0f, 0.0f);  // No gravity by default
        this.world = new World(gravity);
        this.world.setContactListener(this);
        
        this.accumulator = 0.0f;
        
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
    }
    
    @Override
    public void update(float deltaTime) {
        // Fixed timestep physics simulation
        accumulator += deltaTime;
        
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
        
        // Update entity transforms from physics bodies
        super.update(deltaTime);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        CollisionComponent collision = collisionMapper.get(entity);
        
        Body body = collision.getBody();
        if (body == null) {
            // Create physics body if it doesn't exist
            collision.createBody(world, transform.getPosition(), transform.getRotation());
            body = collision.getBody();
        }
        
        // Update transform from physics body
        Vec2 position = body.getPosition();
        transform.setPosition(new Vector2f(position.x, position.y));
        transform.setRotation(body.getAngle());
    }
    
    @Override
    public void beginContact(Contact contact) {
        // Get the colliding entities
        CollisionComponent compA = (CollisionComponent) contact.getFixtureA().getBody().getUserData();
        CollisionComponent compB = (CollisionComponent) contact.getFixtureB().getBody().getUserData();
        
        if (compA != null && compB != null) {
            // Handle collision events here
            // You can get the entities and other components to handle gameplay logic
            // For example: damage dealing, projectile hits, etc.
        }
    }
    
    @Override
    public void endContact(Contact contact) {
        // Handle end of collision if needed
    }
    
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Modify collision behavior before physics solve if needed
    }
    
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Handle post-collision effects if needed
    }
    
    public void setGravity(float x, float y) {
        world.setGravity(new Vec2(x, y));
    }
    
    public World getPhysicsWorld() {
        return world;
    }
    
    @Override
    public void removedFromEngine(com.badlogic.ashley.core.Engine engine) {
        // Clean up physics bodies when system is removed
        for (Entity entity : getEntities()) {
            CollisionComponent collision = collisionMapper.get(entity);
            if (collision != null) {
                collision.destroyBody(world);
            }
        }
        super.removedFromEngine(engine);
    }
    
    public World getWorld() {
        return world;
    }
} 