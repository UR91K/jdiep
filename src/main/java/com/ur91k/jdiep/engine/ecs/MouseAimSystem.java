package com.ur91k.jdiep.engine.ecs;

import com.ur91k.jdiep.engine.core.Input;
import org.joml.Vector2f;

public class MouseAimSystem extends System {
    private final Input input;
    private final int windowHeight;

    public MouseAimSystem(Input input, int windowHeight) {
        this.input = input;
        this.windowHeight = windowHeight;
    }

    @Override
    public void update() {
        var aimingEntities = world.getEntitiesWith(
            TransformComponent.class,
            MouseAimComponent.class
        );

        for (Entity entity : aimingEntities) {
            TransformComponent tankTransform = entity.getComponent(TransformComponent.class);
            MouseAimComponent mouseAim = entity.getComponent(MouseAimComponent.class);
            Entity turret = mouseAim.getTarget();
            
            if (turret != null) {
                // Get tank's center position
                Vector2f tankCenter = tankTransform.getPosition();
                
                // Get mouse position and flip Y coordinate
                Vector2f mousePos = input.getMousePosition();
                mousePos.y = windowHeight - mousePos.y;  // Convert to screen space
                
                // Calculate direction from tank center to mouse
                Vector2f direction = new Vector2f(mousePos).sub(tankCenter);
                
                // Calculate angle to mouse
                float targetAngle = (float) Math.atan2(direction.y, direction.x);
                
                // Set the local rotation directly
                ParentComponent parentComp = turret.getComponent(ParentComponent.class);
                if (parentComp != null) {
                    parentComp.setLocalRotation(targetAngle);
                }
            }
        }
    }
} 