package com.ur91k.jdiep.ecs.systems.render;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import com.ur91k.jdiep.graphics.core.Renderer;

public class RenderingSystem extends SortedIteratingSystem {
    private final Renderer renderer;
    private final Input input;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<ShapeComponent> shapeMapper;
    private final ComponentMapper<ColorComponent> colorMapper;
    private final ComponentMapper<RenderLayer> layerMapper;

    public RenderingSystem(Renderer renderer, Input input) {
        super(Family.all(TransformComponent.class, ShapeComponent.class, ColorComponent.class, RenderLayer.class).get(),
              (e1, e2) -> {
                  RenderLayer layer1 = e1.getComponent(RenderLayer.class);
                  RenderLayer layer2 = e2.getComponent(RenderLayer.class);
                  return layer1.getLayer() - layer2.getLayer();
              });
        
        this.renderer = renderer;
        this.input = input;
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.shapeMapper = ComponentMapper.getFor(ShapeComponent.class);
        this.colorMapper = ComponentMapper.getFor(ColorComponent.class);
        this.layerMapper = ComponentMapper.getFor(RenderLayer.class);
    }

    @Override
    public void update(float deltaTime) {
        // Draw background grid first
        renderer.drawGrid();
        
        // Then draw all entities
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        ShapeComponent shape = shapeMapper.get(entity);
        ColorComponent color = colorMapper.get(entity);
        
        // Render based on shape type
        switch (shape.getType()) {
            case CIRCLE:
                renderer.drawCircle(
                    transform.getPosition(),
                    shape.getRadius(),
                    color.getFillColor(),
                    color.getOutlineWidth(),
                    true  // Always fill shapes
                );
                // Draw outline if width > 0
                if (color.getOutlineWidth() > 0) {
                    renderer.drawCircle(
                        transform.getPosition(),
                        shape.getRadius(),
                        color.getOutlineColor(),
                        color.getOutlineWidth(),
                        false
                    );
                }
                break;
                
            case RECTANGLE:
                renderer.drawRectangle(
                    transform.getPosition(),
                    shape.getDimensions(),
                    transform.getRotation(),
                    color.getFillColor(),
                    color.getOutlineWidth(),
                    true  // Always fill shapes
                );
                // Draw outline if width > 0
                if (color.getOutlineWidth() > 0) {
                    renderer.drawRectangle(
                        transform.getPosition(),
                        shape.getDimensions(),
                        transform.getRotation(),
                        color.getOutlineColor(),
                        color.getOutlineWidth(),
                        false
                    );
                }
                break;
                
            case POLYGON:
                renderer.drawPolygon(
                    transform.getPosition(),
                    shape.getVertices(),
                    transform.getRotation(),
                    color.getFillColor(),
                    color.getOutlineWidth(),
                    true  // Always fill shapes
                );
                // Draw outline if width > 0
                if (color.getOutlineWidth() > 0) {
                    renderer.drawPolygon(
                        transform.getPosition(),
                        shape.getVertices(),
                        transform.getRotation(),
                        color.getOutlineColor(),
                        color.getOutlineWidth(),
                        false
                    );
                }
                break;
        }
    }
} 