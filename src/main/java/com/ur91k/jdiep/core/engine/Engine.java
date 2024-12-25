package com.ur91k.jdiep.core.engine;

import com.ur91k.jdiep.core.logging.Logger;
import com.ur91k.jdiep.core.time.Time;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.core.window.Window;
import com.ur91k.jdiep.debug.components.DebugGraphComponent;
import com.ur91k.jdiep.debug.factories.DebugFactory;
import com.ur91k.jdiep.debug.systems.DebugDrawSystem;
import com.ur91k.jdiep.debug.systems.DebugGraphSystem;
import com.ur91k.jdiep.debug.systems.LabelSystem;
import com.ur91k.jdiep.engine.graphics.config.RenderingConstants;
import com.ur91k.jdiep.engine.graphics.text.TextRenderer;
import com.ur91k.jdiep.engine.ecs.factories.CameraFactory;
import com.ur91k.jdiep.engine.ecs.factories.TankFactory;
import com.ur91k.jdiep.engine.ecs.components.movement.MovementComponent;
import com.ur91k.jdiep.engine.ecs.core.Entity;
import com.ur91k.jdiep.engine.ecs.core.World;
import com.ur91k.jdiep.engine.ecs.systems.camera.CameraSystem;
import com.ur91k.jdiep.engine.ecs.systems.movement.*;
import com.ur91k.jdiep.engine.ecs.systems.render.EntityRenderSystem;
import com.ur91k.jdiep.engine.ecs.systems.render.RenderSystem;

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
    private TextRenderer textRenderer;
    private World world;
    private TankFactory tankFactory;
    private CameraFactory cameraFactory;
    private DebugFactory debugFactory;
    private boolean running;
    private boolean debugMode;

    // Debug systems
    private DebugDrawSystem debugDrawSystem;
    private DebugGraphSystem debugGraphSystem;
    private LabelSystem labelSystem;
    @SuppressWarnings("unused")
    private Entity fpsMonitor;
    private Entity frameTimeGraph;
    private Entity velocityGraph;
    private Entity playerTank;

    public Engine(String title, int width, int height, boolean debugMode, int maxDebugFrames, Logger.Level logLevel) {
        Logger.setGlobalMinimumLevel(logLevel);
        Logger.useColors(true);
        Logger.showTimestamp(true);
        
        this.debugMode = debugMode;
        
        window = new Window(title, width, height);
        input = new Input();
    }
    
    private void init() {
        logger.info("Initializing engine...");
        window.init();
        input.init(window.getHandle());
        Time.init();
        
        // Initialize core systems
        world = new World();
        renderSystem = new RenderSystem(world, window.getWidth(), window.getHeight(), input);
        entityRenderSystem = new EntityRenderSystem(renderSystem);
        textRenderer = new TextRenderer(window.getWidth(), window.getHeight());
        
        // Set up window resize handler
        window.setResizeCallback((width, height) -> {
            logger.debug("Window resized to {}x{}", width, height);
            renderSystem.handleResize(width, height);
            textRenderer.handleResize(width, height);
        });
        
        // Initialize debug systems
        debugDrawSystem = new DebugDrawSystem(world, renderSystem);
        debugGraphSystem = new DebugGraphSystem(world, renderSystem);
        labelSystem = new LabelSystem(world, textRenderer);
        
        // Set debug mode
        debugDrawSystem.setDebugMode(debugMode);
        debugGraphSystem.setDebugMode(debugMode);
        labelSystem.setDebugMode(debugMode);
        
        // Initialize other systems
        parentSystem = new ParentSystem();
        mouseAimSystem = new MouseAimSystem(input);
        cameraSystem = new CameraSystem(input);
        
        // Initialize movement systems
        MovementInputSystem movementInputSystem = new MovementInputSystem(world, input);
        MovementStateSystem movementStateSystem = new MovementStateSystem(world, true);
        
        // Add systems in correct update order
        world.addSystem(movementInputSystem);   // 1. Handle input
        world.addSystem(movementStateSystem);   // 2. Update movement state
        world.addSystem(mouseAimSystem);        // 3. Update rotations
        world.addSystem(parentSystem);          // 4. Update child positions/rotations
        world.addSystem(cameraSystem);          // 5. Update camera
        world.addSystem(debugDrawSystem);       // 6. Debug visualization
        world.addSystem(debugGraphSystem);      // 7. Debug graphs
        world.addSystem(labelSystem);           // 8. Debug labels
        world.addSystem(entityRenderSystem);    // 9. Render everything
        
        // Initialize factories
        tankFactory = new TankFactory(world);
        cameraFactory = new CameraFactory(world);
        debugFactory = new DebugFactory(world);
        
        // Create debug entities
        createDebugEntities();
        
        // Create test entities
        createTestEntities();
        
        running = true;
        
        glClearColor(
            RenderingConstants.BACKGROUND_COLOR.x,
            RenderingConstants.BACKGROUND_COLOR.y,
            RenderingConstants.BACKGROUND_COLOR.z,
            RenderingConstants.BACKGROUND_COLOR.w
        );
        
        logger.info("Engine initialized successfully");
    }

    private void createDebugEntities() {
        // FPS counter
        fpsMonitor = debugFactory.createDebugMonitor(
            "FPS",
            () -> String.valueOf(Time.getFPS())
        );
        
        // Frame time graph
        frameTimeGraph = debugFactory.createPerformanceGraph(
            "Frame Time",
            new Vector2f(10, 50)
        );

        // Velocity graph
        velocityGraph = debugFactory.createPerformanceGraph(
            "Velocity",
            new Vector2f(10, 200)
        );
    }

    @SuppressWarnings("unused")
    private void createTestEntities() {
        // Create a player-controlled twin tank at world origin
        playerTank = tankFactory.makePlayerControlled(
            tankFactory.createTwinTank(new Vector2f(0, 0))
        );
        
        // Add debug visualizations
        playerTank.addComponent(debugFactory.createVelocityVisualizer(playerTank));
        playerTank.addComponent(debugFactory.createHitboxVisualizer(playerTank, 30.0f));
        debugFactory.createEntityLabel(playerTank, "Player Tank");
        
        // Create camera at world origin
        Entity camera = cameraFactory.createCamera(new Vector2f(0, 0));
    }
    
    private void gameLoop() {
        while (running && !window.shouldClose()) {
            Time.update();
            input.update();
            
            // Toggle debug mode with F3
            if (input.isKeyJustPressed(GLFW_KEY_F3)) {
                debugMode = !debugMode;
                debugDrawSystem.setDebugMode(debugMode);
                debugGraphSystem.setDebugMode(debugMode);
                labelSystem.setDebugMode(debugMode);
                logger.debug("Debug mode: {}", debugMode);
            }
            
            // Update debug graphs
            if (debugMode) {
                if (frameTimeGraph != null) {
                    frameTimeGraph.getComponent(DebugGraphComponent.class)
                        .addValue((float)Time.getDeltaTime() * 1000);
                }
                
                if (velocityGraph != null && playerTank != null) {
                    MovementComponent movement = playerTank.getComponent(MovementComponent.class);
                    if (movement != null) {
                        velocityGraph.getComponent(DebugGraphComponent.class)
                            .addValue(movement.getVelocity().length());
                    }
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
        textRenderer.cleanup();
        renderSystem.cleanup();
        window.cleanup();
        logger.info("Engine cleanup complete");
    }
} 