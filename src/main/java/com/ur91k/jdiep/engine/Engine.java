package com.ur91k.jdiep.engine;

import com.ur91k.jdiep.engine.core.Window;
import com.ur91k.jdiep.engine.core.Input;
import com.ur91k.jdiep.engine.debug.DebugOverlay;
import com.ur91k.jdiep.engine.core.Time;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.graphics.RenderingConstants;
import com.ur91k.jdiep.engine.ecs.*;
import com.ur91k.jdiep.engine.ecs.entities.TankFactory;
import com.ur91k.jdiep.engine.ecs.entities.CameraFactory;
import com.ur91k.jdiep.engine.ecs.entities.base.Entity;
import com.ur91k.jdiep.engine.ecs.systems.EntityRenderSystem;
import com.ur91k.jdiep.engine.ecs.systems.MouseAimSystem;
import com.ur91k.jdiep.engine.ecs.systems.ParentSystem;
import com.ur91k.jdiep.engine.ecs.systems.RenderSystem;
import com.ur91k.jdiep.engine.ecs.systems.CameraSystem;
import com.ur91k.jdiep.engine.ecs.components.*;
import com.ur91k.jdiep.engine.ecs.systems.movement.MovementInputSystem;
import com.ur91k.jdiep.engine.ecs.systems.movement.MovementStateSystem;
import com.ur91k.jdiep.engine.debug.DebugSystem;
import com.ur91k.jdiep.engine.graphics.RenderLayer;

import org.joml.Vector2f;

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
    private CameraSystem cameraSystem;
    private DebugOverlay debugOverlay;
    private World world;
    private TankFactory tankFactory;
    private CameraFactory cameraFactory;
    private boolean running;
    private int frameCount;
    private boolean debugMode;
    private int maxDebugFrames;

    public Engine(String title, int width, int height, boolean debugMode, int maxDebugFrames, Logger.Level logLevel) {
        // Configure logging first
        Logger.setGlobalMinimumLevel(logLevel);
        Logger.useColors(true);
        Logger.showTimestamp(true);
        
        this.debugMode = debugMode;
        this.maxDebugFrames = maxDebugFrames;
        
        window = new Window(title, width, height);
        input = new Input();
    }
    
    private void init() {
        logger.info("Initializing engine...");
        window.init();
        input.init(window.getHandle());
        Time.init();
        world = new World();  // Create World first
        
        // Initialize all systems
        renderSystem = new RenderSystem(world, window.getWidth(), window.getHeight(), input);
        entityRenderSystem = new EntityRenderSystem(renderSystem);
        parentSystem = new ParentSystem();
        mouseAimSystem = new MouseAimSystem(input);
        cameraSystem = new CameraSystem(input);
        debugOverlay = new DebugOverlay(window.getWidth(), window.getHeight());
        
        // Replace old movement system with new ones
        MovementInputSystem movementInputSystem = new MovementInputSystem(world, input);
        MovementStateSystem movementStateSystem = new MovementStateSystem(world, true);
        DebugSystem debugSystem = new DebugSystem(world, debugOverlay);
        
        // Add systems in correct update order
        world.addSystem(movementInputSystem);   // 1. Handle input
        world.addSystem(movementStateSystem);   // 2. Update movement state
        world.addSystem(mouseAimSystem);        // 3. Update rotations
        world.addSystem(parentSystem);          // 4. Update child positions/rotations
        world.addSystem(cameraSystem);          // 5. Update camera
        world.addSystem(debugSystem);           // 6. Update debug info
        world.addSystem(entityRenderSystem);    // 7. Render everything
        
        // Initialize factories
        tankFactory = new TankFactory(world);
        cameraFactory = new CameraFactory(world);
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

    @SuppressWarnings("unused")
    private void createTestEntities() {
        // Create a player-controlled twin tank at world origin
        Entity playerTank = tankFactory.makePlayerControlled(
            tankFactory.createTwinTank(new Vector2f(0, 0))
        );
        
        // Create camera at world origin
        Entity camera = cameraFactory.createCamera(new Vector2f(0, 0));
    }
    
    private void gameLoop() {
        while (running && !window.shouldClose()) {
            Time.update();
            input.update();
            
            if (input.isKeyJustPressed(GLFW_KEY_F3)) {
                logger.debug("F3 key pressed");
                debugOverlay.toggleVisibility();
            }
            
            if (debugOverlay.isVisible()) {
                debugOverlay.setInfo("FPS", String.valueOf(Time.getFPS()));
                debugOverlay.updateInputDebug(input);

                // Update camera debug info
                var cameras = world.getEntitiesWith(CameraComponent.class);
                if (!cameras.isEmpty()) {
                    debugOverlay.updateCameraDebug(cameras.iterator().next());
                }
            }
            
            // Debug entity state before rendering
            if (debugMode) {
                var renderables = world.getEntitiesWith(
                    TransformComponent.class,
                    ShapeComponent.class,
                    ColorComponent.class,
                    RenderLayer.class
                );
                
                logger.debug("Found {} renderable entities", renderables.size());
                for (Entity entity : renderables) {
                    ShapeComponent shape = entity.getComponent(ShapeComponent.class);
                    RenderLayer layer = entity.getComponent(RenderLayer.class);
                    TransformComponent transform = entity.getComponent(TransformComponent.class);
                    logger.debug("Entity {}: shape={}, layer={}, pos={}, rot={}",
                        entity.getId(),
                        shape.getType(),
                        layer.getLayer(),
                        transform.getPosition(),
                        transform.getRotation()
                    );
                }
            }
            
            renderSystem.beginFrame();
            renderSystem.renderGrid(
                1024,           // Number of vertical lines
                1024,           // Number of horizontal lines
                25,             // Grid size
                RenderingConstants.GRID_COLOR
            );
            
            world.update();
            
            // Enable proper blending for text rendering
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            debugOverlay.render();
            glDisable(GL_BLEND);
            
            window.update();
            frameCount++;
            
            if (debugMode) {
                logger.debug("Debug mode frame count: {}", frameCount);
                if (frameCount >= maxDebugFrames) {
                    logger.debug("Debug frames complete. Stopping game loop");
                    running = false;
                }
            }
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