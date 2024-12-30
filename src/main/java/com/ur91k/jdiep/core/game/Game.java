package com.ur91k.jdiep.core.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ur91k.jdiep.core.window.Input;
import com.ur91k.jdiep.core.window.Window;
import com.ur91k.jdiep.debug.ImGuiDebugManager;
import com.ur91k.jdiep.ecs.factories.CameraFactory;
import com.ur91k.jdiep.ecs.factories.FoodFactory;
import com.ur91k.jdiep.ecs.factories.TankFactory;
import com.ur91k.jdiep.ecs.systems.camera.CameraSystem;
import com.ur91k.jdiep.ecs.systems.gameplay.FoodDriftSystem;
import com.ur91k.jdiep.ecs.systems.movement.LocalPlayerControlSystem;
import com.ur91k.jdiep.ecs.systems.movement.TankMovementSystem;
import com.ur91k.jdiep.ecs.systems.movement.MovementSystem;
import com.ur91k.jdiep.ecs.systems.physics.PhysicsSystem;
import com.ur91k.jdiep.ecs.systems.transform.ParentSystem;
import com.ur91k.jdiep.ecs.systems.render.RenderingSystem;
import com.ur91k.jdiep.graphics.core.OpenGLRenderer;
import com.ur91k.jdiep.graphics.core.Renderer;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

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
        ashley.addSystem(new LocalPlayerControlSystem(input, debugManager));  // Update player input
        ashley.addSystem(new TankMovementSystem());            // Apply tank movement
        ashley.addSystem(new MovementSystem());                // Apply velocity to position
        ashley.addSystem(new FoodDriftSystem(physicsSystem.getWorld())); // Food movement
        ashley.addSystem(new ParentSystem());                  // Update hierarchies
        ashley.addSystem(new CameraSystem(input));             // Update camera
        ashley.addSystem(new RenderingSystem(renderer, input));// Render last
        
        Logger.info("Game systems initialized");
    }

    private void createInitialEntities() {
        // Create factories with physics world
        PhysicsSystem physicsSystem = ashley.getSystem(PhysicsSystem.class);
        TankFactory tankFactory = new TankFactory(ashley, debugManager);  // Pass debugManager
        FoodFactory foodFactory = new FoodFactory(ashley);
        CameraFactory cameraFactory = new CameraFactory(ashley);

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