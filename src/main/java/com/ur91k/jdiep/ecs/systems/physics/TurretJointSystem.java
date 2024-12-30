package com.ur91k.jdiep.ecs.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.ecs.components.gameplay.TurretComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.TurretJointComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.joml.Vector2f;

/**
 * System that manages the physics joints between turrets and tanks.
 * Handles joint creation, motor control, and angle limits.
 */
public class TurretJointSystem extends IteratingSystem {
    private final World physicsWorld;
    private final ComponentMapper<TurretComponent> turretMapper;
    private final ComponentMapper<TurretJointComponent> jointMapper;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    
    public TurretJointSystem(World physicsWorld) {
        super(Family.all(
            TurretComponent.class,
            TurretJointComponent.class,
            CollisionComponent.class,
            TransformComponent.class
        ).get());
        
        this.physicsWorld = physicsWorld;
        this.turretMapper = ComponentMapper.getFor(TurretComponent.class);
        this.jointMapper = ComponentMapper.getFor(TurretJointComponent.class);
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TurretComponent turret = turretMapper.get(entity);
        TurretJointComponent jointComp = jointMapper.get(entity);
        CollisionComponent collision = collisionMapper.get(entity);
        
        // If we don't have a joint yet, try to create one
        if (jointComp.getJoint() == null) {
            createJoint(entity, turret, jointComp, collision);
            return;
        }
        
        // Get the current joint angle
        RevoluteJoint joint = jointComp.getJoint();
        float currentAngle = joint.getJointAngle();
        
        // Calculate the angle difference we need to cover
        float angleDiff = jointComp.calculateAngleDifference(currentAngle);
        
        // If we're not at the target angle, apply motor force
        if (Math.abs(angleDiff) > 0.01f) {  // Small threshold to prevent jitter
            // Set motor speed based on how far we need to turn
            float speed = Math.signum(angleDiff) * jointComp.getMotorSpeed();
            joint.setMotorSpeed(speed);
            
            // Enable the motor
            if (!joint.isMotorEnabled()) {
                joint.enableMotor(true);
            }
        } else {
            // At target angle, stop the motor
            joint.setMotorSpeed(0);
        }
    }
    
    private void createJoint(Entity entity, TurretComponent turret, 
                           TurretJointComponent jointComp, CollisionComponent collision) {
        Entity tankEntity = turret.getTankBody();
        if (tankEntity == null) return;
        
        CollisionComponent tankCollision = tankEntity.getComponent(CollisionComponent.class);
        if (tankCollision == null || tankCollision.getBody() == null || collision.getBody() == null) {
            return;  // Bodies not ready yet
        }
        
        // Create the revolute joint definition
        RevoluteJointDef jointDef = new RevoluteJointDef();
        
        // Configure the bodies
        jointDef.bodyA = tankCollision.getBody();  // Tank
        jointDef.bodyB = collision.getBody();      // Turret
        
        // Set the anchor point (in world coordinates)
        Vector2f mountPoint = jointComp.getMountPoint();
        Vec2 anchor = new Vec2(mountPoint.x, mountPoint.y);
        jointDef.localAnchorA.set(jointDef.bodyA.getLocalPoint(anchor));
        jointDef.localAnchorB.set(jointDef.bodyB.getLocalPoint(anchor));
        
        // Set the reference angle to maintain initial orientation
        float referenceAngle = jointComp.getMountAngleOffset();
        jointDef.referenceAngle = referenceAngle;
        
        // Configure joint properties
        jointDef.enableMotor = jointComp.isMotorEnabled();
        jointDef.maxMotorTorque = jointComp.getMaxMotorTorque();
        jointDef.motorSpeed = jointComp.getMotorSpeed();
        
        // Set angle limits
        jointDef.enableLimit = true;
        jointDef.lowerAngle = jointComp.getLowerAngleLimit();
        jointDef.upperAngle = jointComp.getUpperAngleLimit();
        
        // Create the joint
        RevoluteJoint joint = (RevoluteJoint) physicsWorld.createJoint(jointDef);
        jointComp.setJoint(joint);
    }
    
    @Override
    public void removedFromEngine(com.badlogic.ashley.core.Engine engine) {
        // Clean up joints when system is removed
        for (Entity entity : getEntities()) {
            TurretJointComponent jointComp = jointMapper.get(entity);
            if (jointComp.getJoint() != null) {
                physicsWorld.destroyJoint(jointComp.getJoint());
                jointComp.setJoint(null);
            }
        }
        super.removedFromEngine(engine);
    }
} 