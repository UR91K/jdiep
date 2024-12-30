package com.ur91k.jdiep.core.window;

import com.ur91k.jdiep.game.config.GameUnits;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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
    private final boolean[] prevKeys = new boolean[GLFW_KEY_LAST];
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
        
        // Get raw mouse position in screen coordinates
        float screenX = mousePos.x;
        float screenY = mousePos.y;
        
        // Convert to normalized device coordinates (-1 to 1)
        float ndcX = (2.0f * screenX) / window.getWidth() - 1.0f;
        float ndcY = 1.0f - (2.0f * screenY) / window.getHeight();  // Flip Y since screen coordinates are Y-down
        
        // Get zoom from view matrix
        float zoom = viewMatrix.m00();  // Scale is in the diagonal elements
        
        // Get the inverse of the combined view-projection matrix
        Matrix4f invViewProj = new Matrix4f(projectionMatrix).mul(viewMatrix).invert();
        
        // Transform NDC coordinates to world space
        Vector4f worldPos = new Vector4f(ndcX, ndcY, 0, 1.0f).mul(invViewProj);
        
        return new Vector2f(worldPos.x, worldPos.y);
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

    public void update() {
        // Store previous frame's key states
        System.arraycopy(keys, 0, prevKeys, 0, GLFW_KEY_LAST);
    }
    
    public boolean isKeyJustPressed(int key) {
        return key >= 0 && key < GLFW_KEY_LAST && keys[key] && !prevKeys[key];
    }
}