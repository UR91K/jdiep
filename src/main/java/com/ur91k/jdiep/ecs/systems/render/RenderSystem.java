package com.ur91k.jdiep.ecs.systems.render;

import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.core.Renderer;
import org.joml.Vector2f;

public class RenderSystem {
    private final Renderer renderer;

    public RenderSystem(Renderer renderer) {
        this.renderer = renderer;
    }

    public void renderCircle(TransformComponent transform, ShapeComponent shape, ColorComponent color) {
        renderer.drawCircle(
            transform.getPosition(),
            shape.getRadius(),
            color.getColor()
        );
    }

    public void renderRectangle(TransformComponent transform, ShapeComponent shape, ColorComponent color) {
        renderer.drawRectangle(
            transform.getPosition(),
            shape.getDimensions(),
            transform.getRotation(),
            color.getColor()
        );
    }

    public void renderPolygon(TransformComponent transform, ShapeComponent shape, ColorComponent color) {
        Vector2f[] vertices = shape.getVertices();
        if (vertices != null) {
            renderer.drawPolygon(
                transform.getPosition(),
                vertices,
                transform.getRotation(),
                color.getColor()
            );
        }
    }
} 