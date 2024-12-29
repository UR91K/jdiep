package com.ur91k.jdiep.ecs.systems.render;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import com.ur91k.jdiep.graphics.core.Renderer;

public class RenderingSystem extends SortedIteratingSystem {
    private final RenderSystem renderSystem;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<ShapeComponent> shapeMapper;
    private final ComponentMapper<ColorComponent> colorMapper;
    private final ComponentMapper<RenderLayer> layerMapper;

    public RenderingSystem(Renderer renderer) {
        super(Family.all(TransformComponent.class, ShapeComponent.class, ColorComponent.class, RenderLayer.class).get(),
              (e1, e2) -> {
                  RenderLayer layer1 = e1.getComponent(RenderLayer.class);
                  RenderLayer layer2 = e2.getComponent(RenderLayer.class);
                  return layer1.getLayer() - layer2.getLayer();
              });
        
        this.renderSystem = new RenderSystem(renderer);
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.shapeMapper = ComponentMapper.getFor(ShapeComponent.class);
        this.colorMapper = ComponentMapper.getFor(ColorComponent.class);
        this.layerMapper = ComponentMapper.getFor(RenderLayer.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        ShapeComponent shape = shapeMapper.get(entity);
        ColorComponent color = colorMapper.get(entity);
        
        // Render based on shape type
        switch (shape.getType()) {
            case CIRCLE:
                renderSystem.renderCircle(transform, shape, color);
                break;
            case RECTANGLE:
                renderSystem.renderRectangle(transform, shape, color);
                break;
            case POLYGON:
                renderSystem.renderPolygon(transform, shape, color);
                break;
        }
    }
} 