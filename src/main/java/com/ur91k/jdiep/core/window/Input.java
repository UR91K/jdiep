package com.ur91k.jdiep.core.window;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private final Window window;
    private final Vector2f mousePos = new Vector2f();
    private final boolean[] keys = new boolean[GLFW_KEY_LAST];
    private final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private float scrollY;
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    
    public Input() {
        Logger.debug("Initializing input system");
        this.window = null;
    }
    
    public Input(Window window) {
        Logger.debug("Initializing input system for window");
        this.window = window;
        setupCallbacks();
    }
    
    private void setupCallbacks() {
        if (window == null) return;
        
        long windowHandle = window.getHandle();
        
        // Key callback
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key >= 0 && key < GLFW_KEY_LAST) {
                keys[key] = action != GLFW_RELEASE;
            }
        });
        
        // Mouse button callback
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (button >= 0 && button < GLFW_MOUSE_BUTTON_LAST) {
                mouseButtons[button] = action != GLFW_RELEASE;
            }
        });
        
        // Cursor position callback
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            mousePos.set((float)xpos, (float)ypos);
        });

        // Scroll callback
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            scrollY = (float)yoffset;
        });
    }
    
    public boolean isKeyPressed(int key) {
        return key >= 0 && key < GLFW_KEY_LAST && keys[key];
    }
    
    public boolean isMouseButtonPressed(int button) {
        return button >= 0 && button < GLFW_MOUSE_BUTTON_LAST && mouseButtons[button];
    }
    
    public Vector2f getMousePosition() {
        return new Vector2f(mousePos);
    }

    public Vector2f getWorldMousePosition() {
        if (window == null) return new Vector2f();
        
        // Get window center in screen coordinates
        float centerX = window.getWidth() / 2.0f;
        float centerY = window.getHeight() / 2.0f;
        
        // Convert mouse position to be relative to center
        float relX = mousePos.x - centerX;
        float relY = centerY - mousePos.y;  // Flip Y since screen coordinates are Y-down
        
        // Apply inverse view matrix transformations
        float zoom = viewMatrix.m00();  // Scale is in the diagonal elements
        float camX = -viewMatrix.m30() / zoom;  // Translation is in the last column
        float camY = -viewMatrix.m31() / zoom;
        
        // Convert to world coordinates
        float worldX = relX / zoom + camX;
        float worldY = relY / zoom + camY;
        
        return new Vector2f(worldX, worldY);
    }

    public float getScrollY() {
        return scrollY;
    }
    
    public void setViewMatrix(Matrix4f view) {
        this.viewMatrix.set(view);
    }
    
    public void setProjectionMatrix(Matrix4f projection) {
        this.projectionMatrix.set(projection);
    }
    
    public void cleanup() {
        if (window == null) return;
        
        long windowHandle = window.getHandle();
        glfwSetKeyCallback(windowHandle, null);
        glfwSetMouseButtonCallback(windowHandle, null);
        glfwSetCursorPosCallback(windowHandle, null);
        glfwSetScrollCallback(windowHandle, null);
    }
}