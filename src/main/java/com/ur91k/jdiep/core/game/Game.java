package com.ur91k.jdiep.core.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.core.window.Window;
import com.ur91k.jdiep.debug.ImGuiDebugManager;
import com.ur91k.jdiep.ecs.factories.CameraFactory;
import com.ur91k.jdiep.ecs.factories.FoodFactory;
import com.ur91k.jdiep.ecs.factories.TankFactory;
import com.ur91k.jdiep.ecs.factories.WorldBoundsFactory;
import com.ur91k.jdiep.ecs.systems.camera.CameraSystem;
import com.ur91k.jdiep.ecs.systems.gameplay.FoodDriftSystem;
import com.ur91k.jdiep.ecs.systems.physics.PhysicsPlayerControlSystem;
import com.ur91k.jdiep.ecs.systems.physics.PhysicsDroneControlSystem;
import com.ur91k.jdiep.ecs.systems.physics.PhysicsSystem;
import com.ur91k.jdiep.ecs.systems.render.RenderingSystem;
import com.ur91k.jdiep.graphics.core.OpenGLRenderer;
import com.ur91k.jdiep.graphics.core.Renderer;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

/**
 * Main game class that handles initialization and the game loop
 */
public class Game {
    // Core components
    private final Window window;
    private final Input input;
    private final Engine ashley;
    private final Renderer renderer;
    private final ImGuiDebugManager debugManager;
    
    // Game entities
    private Entity playerTank;
    private Entity mainCamera;
    
    // Game state
    private boolean running;

    public Game(int windowWidth, int windowHeight) {
        // Initialize core components
        this.window = new Window(windowWidth, windowHeight, "JDiep");
        this.input = new Input(window);  // Pass window to input
        this.ashley = new Engine();
        this.renderer = new OpenGLRenderer(windowWidth, windowHeight, input);
        this.window.setRenderer(renderer);  // Set renderer for resize handling
        this.debugManager = new ImGuiDebugManager();
        this.debugManager.init(window.getHandle());  // Initialize ImGui
        
        // Initialize systems
        initializeSystems();
        
        // Create initial game entities
        createInitialEntities();
        
        Logger.info("Game initialized");
    }

    private void initializeSystems() {
        // Create physics world
        PhysicsSystem physicsSystem = new PhysicsSystem();
        ashley.addSystem(physicsSystem);
        
        // Add systems in priority order
        ashley.addSystem(new PhysicsPlayerControlSystem(input, debugManager));  // Player physics control
        ashley.addSystem(new PhysicsDroneControlSystem());                      // Drone physics control
        ashley.addSystem(new FoodDriftSystem(physicsSystem.getWorld()));       // Food movement
        ashley.addSystem(new CameraSystem(input));                             // Update camera
        ashley.addSystem(new RenderingSystem(renderer, input));                // Render last
        
        Logger.info("Game systems initialized");
        
        /*
         * TODO: Implement these physics-based systems:
         * 
         * 1. TurretJointSystem
         *    - Create revolute joints between tank and turrets
         *    - Apply motor forces for turret rotation
         *    - Handle turret collision interactions
         *    - Manage joint constraints and limits
         * 
         * 2. ProjectilePhysicsSystem
         *    - Handle bullet lifetime and cleanup
         *    - Manage bullet penetration physics
         *    - Handle ricochet mechanics
         *    - Apply damage on collision
         * 
         * 3. CollisionHandlingSystem
         *    - Process collision events from PhysicsSystem
         *    - Apply damage between entities
         *    - Handle special collision cases (bullets, food, etc.)
         *    - Manage collision filtering
         * 
         * 4. PhysicsDebugSystem
         *    - Visualize physics bodies and joints
         *    - Show force vectors and collision points
         *    - Display joint motor stats
         *    - Graph physics performance metrics
         */
    }

    private void createInitialEntities() {
        // Create factories with physics world
        PhysicsSystem physicsSystem = ashley.getSystem(PhysicsSystem.class);
        TankFactory tankFactory = new TankFactory(ashley, debugManager);
        FoodFactory foodFactory = new FoodFactory(ashley);
        CameraFactory cameraFactory = new CameraFactory(ashley);
        WorldBoundsFactory worldBoundsFactory = new WorldBoundsFactory(ashley);

        // Create world boundaries
        worldBoundsFactory.createWorldBounds();
        
        // Create player tank at world origin
        Entity basicTank = tankFactory.createBasicTank(new Vector2f(0, 0));
        playerTank = tankFactory.makePlayerControlled(basicTank);
        
        // Create dummy tank for physics testing
        tankFactory.createBasicTank(new Vector2f(10, 0));  // 100 units to the right of player
        
        // Create some test food
        foodFactory.createTinyFood(new Vector2f(-5, 5));
        foodFactory.createSmallFood(new Vector2f(5, 5));
        
        // Create main camera following the player
        mainCamera = cameraFactory.createFollowCamera(playerTank, 0.1f);
        
        Logger.info("Initial entities created");
    }

    public void start() {
        if (running) {
            Logger.warn("Game is already running");
            return;
        }

        running = true;
        gameLoop();
    }

    private void gameLoop() {
        Logger.info("Starting game loop");

        double lastTime = glfwGetTime();
        float accumulator = 0f;
        float dt = 1.0f / 60.0f;

        while (running && !window.shouldClose()) {
            double currentTime = glfwGetTime();
            float frameTime = (float)(currentTime - lastTime);
            lastTime = currentTime;

            accumulator += frameTime;

            // Process input
            window.pollEvents();
            input.update();  // Update input state
            
            // Update game state with fixed timestep
            while (accumulator >= dt) {
                ashley.update(dt);
                accumulator -= dt;
            }

            // Render
            window.clear();
            ashley.update(0); // Render pass
            debugManager.update();
            window.swapBuffers();
        }

        cleanup();
    }

    private void cleanup() {
        Logger.info("Cleaning up resources");
        debugManager.cleanup();
        ((OpenGLRenderer) renderer).cleanup();
        window.cleanup();
    }

    public void stop() {
        running = false;
    }

    // Getters for essential components
    public Window getWindow() { return window; }
    public Input getInput() { return input; }
    public Engine getEngine() { return ashley; }
    public Entity getPlayerTank() { return playerTank; }
    public Entity getMainCamera() { return mainCamera; }
} 