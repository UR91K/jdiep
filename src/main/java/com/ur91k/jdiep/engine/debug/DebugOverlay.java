package com.ur91k.jdiep.engine.debug;

import com.ur91k.jdiep.engine.graphics.TextRenderer;
import com.ur91k.jdiep.engine.core.Input;
import com.ur91k.jdiep.engine.core.Logger;
import com.ur91k.jdiep.engine.graphics.RenderingConstants;
import com.ur91k.jdiep.engine.ecs.components.CameraComponent;
import com.ur91k.jdiep.engine.ecs.entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;

public class DebugOverlay {
    private static final Logger logger = Logger.getLogger(DebugOverlay.class);
    private boolean visible = false;
    private final Map<String, String> debugInfo = new LinkedHashMap<>();
    private final TextRenderer textRenderer;
    private final Vector4f textColor = RenderingConstants.rgb(0x5b5b5b);
    private static final float LINE_HEIGHT = 16.0f;
    private static final float PADDING = 8.0f;

    public DebugOverlay(int windowWidth, int windowHeight) {
        logger.debug("Initializing debug overlay with dimensions: {}x{}", windowWidth, windowHeight);
        textRenderer = new TextRenderer(windowWidth, windowHeight);
    }

    public void toggleVisibility() {
        visible = !visible;
        logger.debug("Debug overlay visibility toggled: {}", visible);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setInfo(String key, String value) {
        debugInfo.put(key, value);
        logger.trace("Debug info updated - {}: {}", key, value);
    }

    public void updateCameraDebug(Entity cameraEntity) {
        if (!visible || cameraEntity == null) return;

        CameraComponent camera = cameraEntity.getComponent(CameraComponent.class);
        if (camera == null) return;

        Vector2f pos = camera.getPosition();
        Vector2f vel = camera.getVelocity();
        
        setInfo("Camera Position", String.format("%.1f, %.1f", pos.x, pos.y));
        setInfo("Camera Velocity", String.format("%.1f, %.1f", vel.x, vel.y));
        setInfo("Camera Mode", camera.getMode().toString());
        setInfo("Camera Zoom", String.format("%.2fx", camera.getZoom()));
    }

    public void render() {
        if (!visible) return;
        
        logger.trace("Rendering debug overlay");
        
        float y = PADDING;
        for (Map.Entry<String, String> entry : debugInfo.entrySet()) {
            String text = entry.getKey() + ": " + entry.getValue();
            logger.trace("Rendering text: {}", text);
            textRenderer.renderText(text, PADDING, y, textColor);
            y += LINE_HEIGHT;
        }
    }

    public void updateInputDebug(Input input) {
        if (!visible) return;

        // Movement keys state
        List<String> pressedKeys = new ArrayList<>();
        if (input.isKeyPressed(GLFW_KEY_W)) pressedKeys.add("W");
        if (input.isKeyPressed(GLFW_KEY_A)) pressedKeys.add("A");
        if (input.isKeyPressed(GLFW_KEY_S)) pressedKeys.add("S");
        if (input.isKeyPressed(GLFW_KEY_D)) pressedKeys.add("D");

        setInfo("Movement Keys", pressedKeys.isEmpty() ? "None" : String.join(", ", pressedKeys));
        setInfo("Mouse Position", String.format("%.1f, %.1f", input.getMouseX(), input.getMouseY()));
    }

    public void cleanup() {
        logger.debug("Cleaning up debug overlay resources");
        textRenderer.cleanup();
    }
}