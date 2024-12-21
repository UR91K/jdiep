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

        // Create test entity
        Entity testEntity = world.createEntity();
        testEntity.addComponent(new TransformComponent(
            new Vector2f(window.getWidth() / 2, window.getHeight() / 2),  // Center of screen
            new Vector2f(1, 1),  // Normal scale
            0.0f  // No rotation
        ));
        testEntity.addComponent(new ShapeComponent(30.0f));  // Circle with radius 30
        ColorComponent color = new ColorComponent(new Vector4f(0.2f, 0.6f, 1.0f, 1.0f));  // Blue color
        color.setOutline(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), 3.0f);  // Black outline, 3px thick
        testEntity.addComponent(color);
        
        logger.info("Engine initialized successfully");
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