package com.ur91k.jdiep.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.joml.Vector2f;

/**
 * Component that manages the physics joint between a turret and its tank.
 * This component handles the Box2D revolute joint configuration and motor control.
 */
public class TurretJointComponent implements Component {
    private RevoluteJoint joint;              // Box2D joint reference
    private Vector2f mountPoint;              // Local point where turret attaches to tank
    private float mountAngleOffset;           // Angular offset from tank's forward direction
    private float motorSpeed;                 // Target angular velocity (rad/s)
    private float maxMotorTorque;             // Maximum torque the motor can apply
    private float lowerAngleLimit;            // Lower angle limit (radians)
    private float upperAngleLimit;            // Upper angle limit (radians)
    private float targetAngle;                // Current target angle for the motor
    private boolean isMotorEnabled;           // Whether the motor is active
    
    public TurretJointComponent() {
        this.mountPoint = new Vector2f();
        this.mountAngleOffset = 0.0f;
        this.motorSpeed = 2.0f;               // 2 radians per second
        this.maxMotorTorque = 1000.0f;        // Adjust based on testing
        this.lowerAngleLimit = -(float)Math.PI; // Default to full rotation
        this.upperAngleLimit = (float)Math.PI;
        this.targetAngle = 0.0f;
        this.isMotorEnabled = true;
    }
    
    public void init(Vector2f mountPoint, float mountAngleOffset, float motorSpeed, 
                    float maxMotorTorque, float lowerLimit, float upperLimit) {
        this.mountPoint.set(mountPoint);
        this.mountAngleOffset = mountAngleOffset;
        this.motorSpeed = motorSpeed;
        this.maxMotorTorque = maxMotorTorque;
        this.lowerAngleLimit = lowerLimit;
        this.upperAngleLimit = upperLimit;
        this.targetAngle = 0.0f;
        this.isMotorEnabled = true;
    }
    
    public RevoluteJoint getJoint() {
        return joint;
    }
    
    public void setJoint(RevoluteJoint joint) {
        this.joint = joint;
    }
    
    public Vector2f getMountPoint() {
        return new Vector2f(mountPoint);
    }
    
    public float getMountAngleOffset() {
        return mountAngleOffset;
    }
    
    public float getMotorSpeed() {
        return motorSpeed;
    }
    
    public void setMotorSpeed(float speed) {
        this.motorSpeed = speed;
        if (joint != null) {
            joint.setMotorSpeed(speed);
        }
    }
    
    public float getMaxMotorTorque() {
        return maxMotorTorque;
    }
    
    public void setMaxMotorTorque(float torque) {
        this.maxMotorTorque = torque;
        if (joint != null) {
            joint.setMaxMotorTorque(torque);
        }
    }
    
    public float getLowerAngleLimit() {
        return lowerAngleLimit;
    }
    
    public float getUpperAngleLimit() {
        return upperAngleLimit;
    }
    
    public float getTargetAngle() {
        return targetAngle;
    }
    
    public void setTargetAngle(float angle) {
        this.targetAngle = angle;
    }
    
    public boolean isMotorEnabled() {
        return isMotorEnabled;
    }
    
    public void setMotorEnabled(boolean enabled) {
        this.isMotorEnabled = enabled;
        if (joint != null) {
            joint.enableMotor(enabled);
        }
    }
    
    /**
     * Calculates the shortest angular distance to the target angle,
     * taking into account the joint limits if they exist.
     */
    public float calculateAngleDifference(float currentAngle) {
        float diff = targetAngle - currentAngle;
        
        // Normalize to [-π, π]
        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        
        // If we have angle limits, clamp the difference
        if (joint != null && joint.isLimitEnabled()) {
            if (currentAngle + diff < lowerAngleLimit) {
                diff = lowerAngleLimit - currentAngle;
            } else if (currentAngle + diff > upperAngleLimit) {
                diff = upperAngleLimit - currentAngle;
            }
        }
        
        return diff;
    }
} 