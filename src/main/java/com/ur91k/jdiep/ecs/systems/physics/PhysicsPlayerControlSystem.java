package com.ur91k.jdiep.ecs.systems.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.debug.ImGuiDebugManager;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankControllerComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class PhysicsPlayerControlSystem extends IteratingSystem {
    private final Input input;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final ComponentMapper<TankControllerComponent> controllerMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ImGuiDebugManager debugManager;
    
    public PhysicsPlayerControlSystem(Input input, ImGuiDebugManager debugManager) {
        super(Family.all(
            PlayerControlledComponent.class,
            CollisionComponent.class,
            TankControllerComponent.class,
            TransformComponent.class
        ).get());
        
        this.input = input;
        this.debugManager = debugManager;
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        this.controllerMapper = ComponentMapper.getFor(TankControllerComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collision = collisionMapper.get(entity);
        TankControllerComponent controller = controllerMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        if (collision.getBody() == null) {
            return;  // No physics body yet
        }
        
        // Get the physics body
        org.jbox2d.dynamics.Body body = collision.getBody();
        float mass = body.getMass();
        Vec2 center = body.getWorldCenter();
        
        // Handle movement input
        Vector2f moveDir = new Vector2f();
        if (input.isKeyPressed(GLFW_KEY_W)) moveDir.y += 1;
        if (input.isKeyPressed(GLFW_KEY_S)) moveDir.y -= 1;
        if (input.isKeyPressed(GLFW_KEY_A)) moveDir.x -= 1;
        if (input.isKeyPressed(GLFW_KEY_D)) moveDir.x += 1;
        
        // Update controller's move force
        controller.setMoveForce(moveDir);
        
        // Apply movement force if moving
        if (moveDir.length() > 0) {
            moveDir.normalize();
            float force = controller.getMaxForce();
            Vec2 impulse = new Vec2(moveDir.x * force * deltaTime, moveDir.y * force * deltaTime);
            body.applyLinearImpulse(impulse, center);
        }
        
        // Handle aiming with mouse
        Vector2f mousePos = input.getWorldMousePosition();
        Vector2f entityPos = transform.getPosition();
        Vector2f aimDir = new Vector2f(mousePos).sub(entityPos);
        float targetAngle = (float) Math.atan2(aimDir.y, aimDir.x);
        
        // Update controller's target angle
        controller.setTargetAngle(targetAngle);
        
        // Get current angle and normalize difference
        float currentAngle = body.getAngle();
        float angleDiff = normalizeAngle(targetAngle - currentAngle);
        
        // Apply torque based on angle difference, capped by max torque
        float torque = angleDiff * controller.getMaxTorque();
        // Clamp torque to max value
        torque = Math.max(-controller.getMaxTorque(), Math.min(controller.getMaxTorque(), torque));
        body.applyTorque(torque);
        
        // Update shooting state
        controller.setShooting(input.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));
        
        // Debug visualization
        if (debugManager != null) {
            Vec2 velocity = body.getLinearVelocity();
            float angularVelocity = body.getAngularVelocity();
            
            debugManager.addGraphValue("Linear Velocity X", velocity.x);
            debugManager.addGraphValue("Linear Velocity Y", velocity.y);
            debugManager.addGraphValue("Speed", velocity.length());
            debugManager.addGraphValue("Angular Velocity", angularVelocity);
            debugManager.addGraphValue("Angle Difference", angleDiff);
            debugManager.addGraphValue("Applied Torque", torque);
            debugManager.addGraphValue("Applied Force", moveDir.length() * controller.getMaxForce());
        }
    }
    
    // Normalize angle to [-π, π]
    private float normalizeAngle(float angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }
} 