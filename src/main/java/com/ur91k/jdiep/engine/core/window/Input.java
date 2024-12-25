package com.ur91k.jdiep.engine.core.window;

import org.joml.Vector2f;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import com.ur91k.jdiep.engine.core.logging.Logger;
import com.ur91k.jdiep.game.config.GameConstants;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final Logger logger = Logger.getLogger(Input.class);
    private static final int MAX_KEYS = 348;
    private static final int MAX_BUTTONS = 8;
    
    private int windowWidth;
    private int windowHeight;
    private boolean[] keys = new boolean[MAX_KEYS];
    private boolean[] keysJustPressed = new boolean[MAX_KEYS];
    private boolean[] keysPendingPress = new boolean[MAX_KEYS];
    private boolean[] mouseButtons = new boolean[MAX_BUTTONS];
    private boolean[] mouseButtonsJustPressed = new boolean[MAX_BUTTONS];
    private double mouseX, mouseY;
    private float scrollX, scrollY;
    private long windowHandle;
    private final DoubleBuffer mouseXBuffer = BufferUtils.createDoubleBuffer(1);
    private final DoubleBuffer mouseYBuffer = BufferUtils.createDoubleBuffer(1);
    
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    @SuppressWarnings("unused")
    private boolean matricesNeedUpdate = true;

    public void init(long windowHandle) {
        logger.info("Initializing input system...");
        
        this.windowHandle = windowHandle;
        
        // Get initial window dimensions
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(windowHandle, width, height);
        this.windowWidth = width[0];
        this.windowHeight = height[0];
        
        // Add window size callback
        glfwSetWindowSizeCallback(windowHandle, (window, newWidth, newHeight) -> {
            windowWidth = newWidth;
            windowHeight = newHeight;
            logger.debug("Window resized to {}x{}", newWidth, newHeight);
        });
        
        // Key callback
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            logger.trace("Key event: key={}, scancode={}, action={}, mods={}", key, scancode, action, mods);
            if (key >= 0 && key < MAX_KEYS) {
                boolean pressed = action != GLFW_RELEASE;
                if (pressed && !keys[key]) {
                    keysPendingPress[key] = true;
                }
                keys[key] = pressed;
            } else {
                logger.warn("Received key event for invalid key code: {}", key);
            }
        });
        
        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            logger.trace("Mouse button event: button={}, action={}, mods={}", button, action, mods);
            if (button >= 0 && button < MAX_BUTTONS) {
                boolean pressed = action != GLFW_RELEASE;
                mouseButtonsJustPressed[button] = !mouseButtons[button] && pressed;
                mouseButtons[button] = pressed;
            } else {
                logger.warn("Received mouse event for invalid button: {}", button);
            }
        });
        
        // Cursor position callback
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            logger.trace("Cursor position: x={}, y={}", xpos, ypos);
            mouseX = xpos;
            mouseY = ypos;
        });
        
        // Scroll callback
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            logger.trace("Scroll event: x={}, y={}", xoffset, yoffset);
            scrollX = (float)xoffset;
            scrollY = (float)yoffset;
        });
        
        logger.debug("Input system initialized successfully");
    }
    
    public void update() {
        // Clear previous "just pressed" flags
        Arrays.fill(keysJustPressed, false);
        
        // Process pending presses
        for (int i = 0; i < MAX_KEYS; i++) {
            if (keysPendingPress[i]) {
                keysJustPressed[i] = true;
                keysPendingPress[i] = false;
            }
        }
        
        // Reset "just pressed" states
        for (int i = 0; i < MAX_BUTTONS; i++) {
            mouseButtonsJustPressed[i] = false;
        }
        scrollX = scrollY = 0;
        
        glfwGetCursorPos(windowHandle, mouseXBuffer, mouseYBuffer);
    }
    
    public boolean isKeyPressed(int key) {
        if (key < 0 || key >= MAX_KEYS) {
            logger.warn("Attempted to check invalid key code: {}", key);
            return false;
        }
        return keys[key];
    }
    
    public boolean isKeyJustPressed(int key) {
        if (key < 0 || key >= MAX_KEYS) {
            logger.warn("Attempted to check invalid key code: {}", key);
            return false;
        }
        return keysJustPressed[key];
    }
    
    public boolean isMouseButtonPressed(int button) {
        if (button < 0 || button >= MAX_BUTTONS) {
            logger.warn("Attempted to check invalid mouse button: {}", button);
            return false;
        }
        return mouseButtons[button];
    }
    
    public boolean isMouseButtonJustPressed(int button) {
        if (button < 0 || button >= MAX_BUTTONS) {
            logger.warn("Attempted to check invalid mouse button: {}", button);
            return false;
        }
        return mouseButtonsJustPressed[button];
    }
    
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public float getScrollX() { return scrollX; }
    public float getScrollY() { return scrollY; }
    
    public Vector2f getMousePosition() {
        return new Vector2f(
            (float)mouseXBuffer.get(0),
            (float)mouseYBuffer.get(0)
        );
    }

    public void setViewMatrix(Matrix4f view) {
        this.viewMatrix.set(view);
        matricesNeedUpdate = true;
    }

    public void setProjectionMatrix(Matrix4f projection) {
        this.projectionMatrix.set(projection);
        matricesNeedUpdate = true;
    }

    public Vector2f getMouseWorldPosition() {
        Vector2f screenPos = getMousePosition();
        logger.trace("Screen position: {}", screenPos);
        
        // Convert screen coordinates to normalized device coordinates (-1 to 1)
        float ndcX = (2.0f * screenPos.x) / windowWidth - 1.0f;
        float ndcY = 1.0f - (2.0f * screenPos.y) / windowHeight;
        
        // Calculate view dimensions based on aspect ratio
        float aspectRatio = (float)windowWidth / windowHeight;
        float viewWidth = GameConstants.BASE_VIEW_HEIGHT * aspectRatio;
        float viewHeight = GameConstants.BASE_VIEW_HEIGHT;
        
        // Convert to world coordinates
        Vector2f worldPos = new Vector2f(
            ndcX * (viewWidth / 2.0f),
            ndcY * (viewHeight / 2.0f)
        );
        
        // Apply view matrix transformations (zoom and camera position)
        // Check if view matrix is not identity (has some transformation)
        boolean isIdentity = viewMatrix.m00() == 1.0f && 
                           viewMatrix.m11() == 1.0f && 
                           viewMatrix.m22() == 1.0f && 
                           viewMatrix.m33() == 1.0f &&
                           viewMatrix.m30() == 0.0f && 
                           viewMatrix.m31() == 0.0f;
        
        if (!isIdentity) {
            // Extract scale (zoom) from view matrix
            float zoom = viewMatrix.m00();  // Assuming uniform scale
            worldPos.mul(1.0f / zoom);  // Apply inverse zoom
            
            // Extract translation from view matrix
            float cameraX = -viewMatrix.m30();  // Camera position is negative of translation
            float cameraY = -viewMatrix.m31();
            worldPos.add(cameraX, cameraY);  // Add camera position to get world position
        }
        
        logger.trace("World position (after transform): {}", worldPos);
        return worldPos;
    }
}