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
    private ParentSystem parentSystem;
    private MouseAimSystem mouseAimSystem;
    private DebugOverlay debugOverlay;
    private World world;
    private EntityFactory entityFactory;
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
        parentSystem = new ParentSystem();
        mouseAimSystem = new MouseAimSystem(input, window.getHeight());
        debugOverlay = new DebugOverlay(window.getWidth(), window.getHeight());
        world = new World();
        world.addSystem(parentSystem);     // Update parent-child relationships first
        world.addSystem(mouseAimSystem);   // Then update aiming
        world.addSystem(entityRenderSystem);  // Then render
        entityFactory = new EntityFactory(world);
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
        // Create a test tank in the center of the screen
        entityFactory.createTank(new Vector2f(
            window.getWidth() / 2,
            window.getHeight() / 2
        ));
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