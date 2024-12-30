package com.ur91k.jdiep.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.joml.Vector2f;

/**
 * Component that manages a Box2D revolute joint with a motor between two bodies.
 * Typically used for turret-tank connections where physical forces should affect rotation.
 */
public class RevoluteJointComponent implements Component {
    private RevoluteJoint joint;
    private float targetAngle;        // Desired angle in radians
    private float maxMotorTorque;     // Maximum torque the motor can apply
    private float motorSpeed;         // Target motor speed in radians/second
    private boolean isMotorEnabled;   // Whether the motor is currently enabled
    private Vector2f anchorPoint;     // Local anchor point relative to the primary body

    public RevoluteJointComponent() {
        this.targetAngle = 0.0f;
        this.maxMotorTorque = 1000.0f;  // Default value, should be tuned
        this.motorSpeed = 2.0f;         // Default radians/second
        this.isMotorEnabled = true;
        this.anchorPoint = new Vector2f();
    }

    // Joint management
    public RevoluteJoint getJoint() {
        return joint;
    }

    public void setJoint(RevoluteJoint joint) {
        this.joint = joint;
    }

    // Motor control
    public float getTargetAngle() {
        return targetAngle;
    }

    public void setTargetAngle(float angle) {
        this.targetAngle = angle;
        if (joint != null) {
            joint.enableMotor(isMotorEnabled);
            joint.setMotorSpeed(motorSpeed);
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

    public float getMotorSpeed() {
        return motorSpeed;
    }

    public void setMotorSpeed(float speed) {
        this.motorSpeed = speed;
        if (joint != null) {
            joint.setMotorSpeed(speed);
        }
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

    // Anchor point management
    public Vector2f getAnchorPoint() {
        return new Vector2f(anchorPoint);
    }

    public void setAnchorPoint(Vector2f anchor) {
        this.anchorPoint.set(anchor);
    }
} 