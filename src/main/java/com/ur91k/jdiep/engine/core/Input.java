package com.ur91k.jdiep.engine.core;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
    private static final Logger logger = Logger.getLogger(Input.class);
    private static final int MAX_KEYS = 348;
    private static final int MAX_BUTTONS = 8;
    
    private boolean[] keys = new boolean[MAX_KEYS];
    private boolean[] keysJustPressed = new boolean[MAX_KEYS];
    private boolean[] keysPendingPress = new boolean[MAX_KEYS];
    private boolean[] mouseButtons = new boolean[MAX_BUTTONS];
    private boolean[] mouseButtonsJustPressed = new boolean[MAX_BUTTONS];
    private double mouseX, mouseY;
    private double scrollX, scrollY;
    private long windowHandle;
    private final DoubleBuffer mouseXBuffer = BufferUtils.createDoubleBuffer(1);
    private final DoubleBuffer mouseYBuffer = BufferUtils.createDoubleBuffer(1);
    
    public void init(long windowHandle) {
        logger.info("Initializing input system...");
        
        this.windowHandle = windowHandle;
        
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
            scrollX = xoffset;
            scrollY = yoffset;
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
    public double getScrollX() { return scrollX; }
    public double getScrollY() { return scrollY; }
    
    public Vector2f getMousePosition() {
        return new Vector2f(
            (float)mouseXBuffer.get(0),
            (float)mouseYBuffer.get(0)
        );
    }
}