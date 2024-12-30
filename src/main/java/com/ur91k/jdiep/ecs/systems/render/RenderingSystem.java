package com.ur91k.jdiep.ecs.systems.render;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.ecs.components.camera.CameraComponent;
import com.ur91k.jdiep.ecs.components.rendering.ColorComponent;
import com.ur91k.jdiep.ecs.components.rendering.ShapeComponent;
import com.ur91k.jdiep.ecs.components.transform.TransformComponent;
import com.ur91k.jdiep.graphics.core.RenderLayer;
import com.ur91k.jdiep.graphics.core.Renderer;
import com.ur91k.jdiep.game.config.GameUnits;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {
    private final Renderer renderer;
    private final Input input;
    private final ComponentMapper<TransformComponent> transformMapper;
    private final ComponentMapper<ShapeComponent> shapeMapper;
    private final ComponentMapper<ColorComponent> colorMapper;
    private final ComponentMapper<RenderLayer> layerMapper;
    private final ComponentMapper<CameraComponent> cameraMapper;
    private Matrix4f viewMatrix = new Matrix4f();
    
    public RenderingSystem(Renderer renderer, Input input) {
        super(Family.all(TransformComponent.class, ShapeComponent.class, ColorComponent.class, RenderLayer.class).get(),
              (e1, e2) -> {
                  RenderLayer l1 = e1.getComponent(RenderLayer.class);
                  RenderLayer l2 = e2.getComponent(RenderLayer.class);
                  return Float.compare(l1.getLayer(), l2.getLayer());
              });
        
        this.renderer = renderer;
        this.input = input;
        this.transformMapper = ComponentMapper.getFor(TransformComponent.class);
        this.shapeMapper = ComponentMapper.getFor(ShapeComponent.class);
        this.colorMapper = ComponentMapper.getFor(ColorComponent.class);
        this.layerMapper = ComponentMapper.getFor(RenderLayer.class);
        this.cameraMapper = ComponentMapper.getFor(CameraComponent.class);
    }
    
    @Override
    public void update(float deltaTime) {
        // Find active camera and update view matrix
        ImmutableArray<Entity> cameras = getEngine().getEntitiesFor(
            Family.all(CameraComponent.class, TransformComponent.class).get()
        );
        
        if (cameras.size() > 0) {
            Entity camera = cameras.first();
            CameraComponent cameraComp = cameraMapper.get(camera);
            TransformComponent cameraTransform = transformMapper.get(camera);
            
            // Update view matrix
            viewMatrix = new Matrix4f()
                .scale(cameraComp.getZoom())  // Apply zoom first
                .translate(-cameraTransform.getPosition().x, -cameraTransform.getPosition().y, 0);  // Then translate
            
            renderer.setView(viewMatrix);
            input.setViewMatrix(viewMatrix);  // Update input system with same view matrix
        }
        
        // Draw background grid
        renderer.drawGrid();
        
        // Draw entities
        super.update(deltaTime);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);
        ShapeComponent shape = shapeMapper.get(entity);
        ColorComponent color = colorMapper.get(entity);
        
        // Draw shape
        if (shape.getType() == ShapeComponent.ShapeType.CIRCLE) {
            float radius = shape.getRadius();
            if (color.hasOutline()) {
                // Draw outline
                renderer.drawCircle(
                    transform.getPosition(),
                    radius,
                    color.getOutlineColor(),
                    GameUnits.DEFAULT_LINE_THICKNESS,
                    false
                );
            }
            // Draw fill
            renderer.drawCircle(
                transform.getPosition(),
                radius,
                color.getFillColor(),
                GameUnits.DEFAULT_LINE_THICKNESS,
                true
            );
        } else if (shape.getType() == ShapeComponent.ShapeType.RECTANGLE) {
            Vector2f dimensions = shape.getDimensions();
            if (color.hasOutline()) {
                // Draw outline
                renderer.drawRectangle(
                    transform.getPosition(),
                    dimensions,
                    transform.getRotation(),
                    color.getOutlineColor(),
                    GameUnits.DEFAULT_LINE_THICKNESS,
                    false
                );
            }
            // Draw fill
            renderer.drawRectangle(
                transform.getPosition(),
                dimensions,
                transform.getRotation(),
                color.getFillColor(),
                GameUnits.DEFAULT_LINE_THICKNESS,
                true
            );
        } else if (shape.getType() == ShapeComponent.ShapeType.POLYGON) {
            Vector2f[] vertices = shape.getVertices();
            if (color.hasOutline()) {
                // Draw outline
                renderer.drawPolygon(
                    transform.getPosition(),
                    vertices,
                    transform.getRotation(),
                    color.getOutlineColor(),
                    GameUnits.DEFAULT_LINE_THICKNESS,
                    false
                );
            }
            // Draw fill
            renderer.drawPolygon(
                transform.getPosition(),
                vertices,
                transform.getRotation(),
                color.getFillColor(),
                GameUnits.DEFAULT_LINE_THICKNESS,
                true
            );
        }
    }
} 