package com.ur91k.jdiep.ecs.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.RevoluteJointComponent;
import com.ur91k.jdiep.ecs.components.transform.ParentComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.joml.Vector2f;
import org.tinylog.Logger;

public class PhysicsSystem extends IteratingSystem implements ContactListener {
    private static final float TIME_STEP = 1.0f / 60.0f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    
    private final World world;
    private float accumulator;
    
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final ComponentMapper<RevoluteJointComponent> jointMapper;
    private final ComponentMapper<ParentComponent> parentMapper;
    
    public PhysicsSystem() {
        super(Family.all(TransformComponent.class, CollisionComponent.class).get());
        
        // Create Box2D world with gravity
        Vec2 gravity = new Vec2(0.0f, 0.0f);  // No gravity by default
        this.world = new World(gravity);
        this.world.setContactListener(this);
        
        this.accumulator = 0.0f;
        
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        this.jointMapper = ComponentMapper.getFor(RevoluteJointComponent.class);
        this.parentMapper = ComponentMapper.getFor(ParentComponent.class);
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
        RevoluteJointComponent jointComp = jointMapper.get(entity);
        
        Body body = collision.getBody();
        if (body == null) {
            // Create physics body if it doesn't exist
            collision.createBody(world, transform.getPosition(), transform.getRotation());
            body = collision.getBody();
        }
        
        // Create joint if needed
        if (jointComp != null && jointComp.getJoint() == null && body != null) {
            createRevoluteJoint(entity, jointComp, body);
        }
        
        // Update transform from physics body
        Vec2 position = body.getPosition();
        transform.setPosition(new Vector2f(position.x, position.y));
        transform.setRotation(body.getAngle());
    }
    
    private void createRevoluteJoint(Entity entity, RevoluteJointComponent jointComp, Body childBody) {
        // Get parent entity from ParentComponent
        ParentComponent parentComp = parentMapper.get(entity);
        if (parentComp == null) {
            Logger.warn("Cannot create revolute joint without parent relationship");
            return;
        }
        
        Entity parentEntity = parentComp.getParent();
        CollisionComponent parentCollision = collisionMapper.get(parentEntity);
        if (parentCollision == null || parentCollision.getBody() == null) {
            Logger.warn("Parent entity has no physics body");
            return;
        }
        
        Body parentBody = parentCollision.getBody();
        
        // Create joint definition
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = parentBody;
        jointDef.bodyB = childBody;
        
        // Convert anchor point to Box2D coordinates
        Vector2f anchor = jointComp.getAnchorPoint();
        jointDef.localAnchorA.set(anchor.x, anchor.y);  // Parent's local coordinates
        jointDef.localAnchorB.set(0, 0);  // Child's local coordinates (at center)
        
        // Enable the motor
        jointDef.enableMotor = jointComp.isMotorEnabled();
        jointDef.maxMotorTorque = jointComp.getMaxMotorTorque();
        jointDef.motorSpeed = jointComp.getMotorSpeed();
        
        // Create the joint
        RevoluteJoint joint = (RevoluteJoint)world.createJoint(jointDef);
        jointComp.setJoint(joint);
        
        Logger.debug("Created revolute joint between parent and turret");
    }
    
    @Override
    public void removedFromEngine(com.badlogic.ashley.core.Engine engine) {
        // Clean up joints first
        for (Entity entity : engine.getEntitiesFor(Family.all(RevoluteJointComponent.class).get())) {
            RevoluteJointComponent jointComp = jointMapper.get(entity);
            if (jointComp != null && jointComp.getJoint() != null) {
                world.destroyJoint(jointComp.getJoint());
                jointComp.setJoint(null);
            }
        }
        
        // Then clean up bodies
        for (Entity entity : getEntities()) {
            CollisionComponent collision = collisionMapper.get(entity);
            if (collision != null) {
                collision.destroyBody(world);
            }
        }
        super.removedFromEngine(engine);
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
    
    public World getWorld() {
        return world;
    }
} 