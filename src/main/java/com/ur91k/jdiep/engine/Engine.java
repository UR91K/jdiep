package com.ur91k.jdiep.engine;

import com.ur91k.jdiep.engine.core.Window;
import com.ur91k.jdiep.engine.core.Input;
import com.ur91k.jdiep.engine.debug.DebugOverlay;
import com.ur91k.jdiep.engine.core.Time;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.graphics.RenderSystem;
import com.ur91k.jdiep.engine.graphics.RenderingConstants;
import com.ur91k.jdiep.engine.ecs.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Engine {
    private static final Logger logger = Logger.getLogger(Engine.class);
    private Window window;
    private Input input;
    private RenderSystem renderSystem;
    private EntityRenderSystem entityRenderSystem;
    private DebugOverlay debugOverlay;
    private World world;
    private boolean running;
    
    public Engine(String title, int width, int height) {
        // Configure logging first
        Logger.setGlobalMinimumLevel(Logger.Level.ERROR);  // Set to ERROR level globally
        Logger.useColors(true);  // Enable colored output
        Logger.showTimestamp(true);  // Show timestamps in logs
        
        window = new Window(title, width, height);
        input = new Input();
    }
    
    private void init() {
        logger.info("Initializing engine...");
        window.init();
        input.init(window.getHandle());
        Time.init();
        renderSystem = new RenderSystem(window.getWidth(), window.getHeight());
        entityRenderSystem = new EntityRenderSystem(renderSystem);
        debugOverlay = new DebugOverlay(window.getWidth(), window.getHeight());
        world = new World();
        world.addSystem(entityRenderSystem);
        running = true;
        
        glClearColor(
            RenderingConstants.BACKGROUND_COLOR.x,
            RenderingConstants.BACKGROUND_COLOR.y,
            RenderingConstants.BACKGROUND_COLOR.z,
            RenderingConstants.BACKGROUND_COLOR.w
        );

        createTestEntities();
        
        logger.info("Engine initialized successfully");
    }

    private void createTestEntities() {
        float spacing = 100.0f;  // Space between test entities
        float centerX = window.getWidth() / 2;
        float centerY = window.getHeight() / 2;

        // Create circle test entity
        Entity circleEntity = world.createEntity();
        circleEntity.addComponent(new TransformComponent(
            new Vector2f(centerX - spacing, centerY),  // Left of center
            new Vector2f(1, 1),
            0.0f
        ));
        circleEntity.addComponent(new ShapeComponent(30.0f));  // Circle with radius 30
        ColorComponent circleColor = new ColorComponent(new Vector4f(0.2f, 0.6f, 1.0f, 1.0f));
        circleColor.setOutline(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), 3.0f);
        circleEntity.addComponent(circleColor);

        // Create triangle test entity
        Entity triangleEntity = world.createEntity();
        triangleEntity.addComponent(new TransformComponent(
            new Vector2f(centerX, centerY),  // Center
            new Vector2f(1, 1),
            0.0f
        ));
        triangleEntity.addComponent(new ShapeComponent(ShapeComponent.ShapeType.TRIANGLE, 30.0f, 3));
        ColorComponent triangleColor = new ColorComponent(new Vector4f(1.0f, 0.4f, 0.4f, 1.0f));  // Light red
        triangleColor.setOutline(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), 3.0f);
        triangleEntity.addComponent(triangleColor);

        // Create pentagon test entity
        Entity pentagonEntity = world.createEntity();
        pentagonEntity.addComponent(new TransformComponent(
            new Vector2f(centerX + spacing, centerY),  // Right of center
            new Vector2f(1, 1),
            0.0f
        ));
        pentagonEntity.addComponent(new ShapeComponent(ShapeComponent.ShapeType.POLYGON, 30.0f, 5));
        ColorComponent pentagonColor = new ColorComponent(new Vector4f(0.4f, 0.4f, 1.0f, 1.0f));  // Light blue
        pentagonColor.setOutline(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), 3.0f);
        pentagonEntity.addComponent(pentagonColor);
    }
    
    private void gameLoop() {
        while (running && !window.shouldClose()) {
            Time.update();
            input.update();
            
            // Debug: Check for any key press
            for (int key = 0; key < 348; key++) {
                if (input.isKeyJustPressed(key)) {
                    logger.debug("Key pressed: {}", key);
                }
            }
            
            if (input.isKeyJustPressed(GLFW_KEY_F3)) {
                logger.debug("F3 key pressed");
                debugOverlay.toggleVisibility();
            }
            
            if (debugOverlay.isVisible()) {
                debugOverlay.setInfo("F3 State", String.valueOf(input.isKeyPressed(GLFW_KEY_F3)));
                debugOverlay.setInfo("FPS", String.valueOf(Time.getFPS()));
                debugOverlay.updateInputDebug(input);
            }
            
            renderSystem.beginFrame();
            renderSystem.renderGrid(
                window.getWidth(),
                window.getHeight(),
                RenderingConstants.GRID_SIZE,
                RenderingConstants.GRID_COLOR
            );
            
            world.update();
            
            // Enable proper blending for text rendering
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            debugOverlay.render();
            glDisable(GL_BLEND);
            
            window.update();
        }
    }
    
    public void start() {
        init();
        gameLoop();
        cleanup();
    }
    
    private void cleanup() {
        logger.info("Cleaning up engine resources...");
        entityRenderSystem.cleanup();
        debugOverlay.cleanup();
        renderSystem.cleanup();
        window.cleanup();
        logger.info("Engine cleanup complete");
    }
} 