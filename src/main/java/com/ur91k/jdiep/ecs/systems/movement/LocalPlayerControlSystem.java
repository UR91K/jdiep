package com.ur91k.jdiep.ecs.systems.movement;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.debug.ImGuiDebugManager;
import com.ur91k.jdiep.ecs.components.gameplay.PlayerControlledComponent;
import com.ur91k.jdiep.ecs.components.gameplay.TankControllerComponent;
import com.ur91k.jdiep.ecs.components.physics.CollisionComponent;
import com.ur91k.jdiep.ecs.components.physics.VelocityComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class LocalPlayerControlSystem extends IteratingSystem {
    private final Input input;
    private final ComponentMapper<CollisionComponent> collisionMapper;
    private final ComponentMapper<VelocityComponent> velocityMapper;
    private final ComponentMapper<TankControllerComponent> controllerMapper;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ImGuiDebugManager debugManager;
    
    // Store previous values for derivative calculations
    private Vec2 prevVelocity = new Vec2();
    private Vec2 prevAcceleration = new Vec2();
    
    public LocalPlayerControlSystem(Input input, ImGuiDebugManager debugManager) {
        super(Family.all(
            PlayerControlledComponent.class,
            CollisionComponent.class,
            VelocityComponent.class,
            TankControllerComponent.class,
            TransformComponent.class
        ).get());
        
        this.input = input;
        this.debugManager = debugManager;
        this.collisionMapper = ComponentMapper.getFor(CollisionComponent.class);
        this.velocityMapper = ComponentMapper.getFor(VelocityComponent.class);
        this.controllerMapper = ComponentMapper.getFor(TankControllerComponent.class);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent collision = collisionMapper.get(entity);
        VelocityComponent velocity = velocityMapper.get(entity);
        TankControllerComponent controller = controllerMapper.get(entity);
        TransformComponent transform = transformMapper.get(entity);
        
        if (collision.getBody() == null) {
            return;  // No physics body yet
        }
        
        // Handle movement
        Vector2f direction = new Vector2f();
        if (input.isKeyPressed(GLFW_KEY_W)) direction.y += 1;
        if (input.isKeyPressed(GLFW_KEY_S)) direction.y -= 1;
        if (input.isKeyPressed(GLFW_KEY_A)) direction.x -= 1;
        if (input.isKeyPressed(GLFW_KEY_D)) direction.x += 1;
        
        // Normalize direction if moving diagonally
        if (direction.length() > 0) {
            direction.normalize();
        }
        
        // Apply force based on acceleration and mass
        float mass = collision.getBody().getMass();
        float force = velocity.getAcceleration() * mass;  // F = ma
        Vec2 impulse = new Vec2(direction.x * force * deltaTime, direction.y * force * deltaTime);
        Vec2 center = collision.getBody().getWorldCenter();
        collision.getBody().applyLinearImpulse(impulse, center);
        
        // Update aim angle from mouse position
        Vector2f mousePos = input.getWorldMousePosition();
        Vector2f entityPos = transform.getPosition();
        Vector2f aimDir = new Vector2f(mousePos).sub(entityPos);
        float aimAngle = (float) Math.atan2(aimDir.y, aimDir.x);
        controller.setAimAngle(aimAngle);
        
        // Update shooting state
        controller.setShooting(input.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT));
        
        // Calculate and graph derivatives
        if (deltaTime > 0) {
            // Get current velocity
            Vec2 currentVel = collision.getBody().getLinearVelocity();
            
            // Calculate acceleration (change in velocity)
            Vec2 currentAcc = new Vec2();
            currentAcc.x = (currentVel.x - prevVelocity.x) / deltaTime;
            currentAcc.y = (currentVel.y - prevVelocity.y) / deltaTime;
            
            // Calculate jerk (change in acceleration)
            Vec2 jerk = new Vec2();
            jerk.x = (currentAcc.x - prevAcceleration.x) / deltaTime;
            jerk.y = (currentAcc.y - prevAcceleration.y) / deltaTime;
            
            // Update graphs
            debugManager.addGraphValue("Velocity X", currentVel.x);
            debugManager.addGraphValue("Velocity Y", currentVel.y);
            debugManager.addGraphValue("Acceleration X", currentAcc.x);
            debugManager.addGraphValue("Acceleration Y", currentAcc.y);
            debugManager.addGraphValue("Jerk X", jerk.x);
            debugManager.addGraphValue("Jerk Y", jerk.y);
            debugManager.addGraphValue("Speed", currentVel.length());
            
            // Store current values for next frame
            prevVelocity.set(currentVel);
            prevAcceleration.set(currentAcc);
        }
    }
} 