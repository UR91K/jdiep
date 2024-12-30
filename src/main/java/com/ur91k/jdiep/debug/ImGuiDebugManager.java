package com.ur91k.jdiep.debug;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiCond;
import imgui.type.ImFloat;
import imgui.glfw.ImGuiImplGlfw;
import imgui.gl3.ImGuiImplGl3;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class ImGuiDebugManager {
    private final Map<String, List<Float>> graphs = new HashMap<>();
    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Map<String, Object>> entityStates = new HashMap<>();
    private final int maxDataPoints = 100;
    private boolean showDebugWindow = false;
    private boolean showPerformanceWindow = false;
    private boolean showEntityDebugger = false;
    private boolean showTankPhysicsDebugger = false;
    
    // Tank physics debug state - using arrays for ImGui persistence
    private final float[] tankAcceleration = new float[] { 800.0f };
    private final float[] tankFriction = new float[] { 0.05f };
    private final float[] tankLinearDamping = new float[] { 0.2f };
    private final float[] tankAngularDamping = new float[] { 3.0f };
    private final float[] tankDensity = new float[] { 1.0f };
    private final float[] tankRestitution = new float[] { 0.5f };
    private final float[] tankVelocityFriction = new float[] { 0.95f };
    
    public interface TankPhysicsCallback {
        void onTankPhysicsUpdate(float acceleration, float friction,
                               float linearDamping, float angularDamping, float density,
                               float restitution, float velocityFriction);
    }
    
    private TankPhysicsCallback tankPhysicsCallback;
    
    public void setTankPhysicsCallback(TankPhysicsCallback callback) {
        this.tankPhysicsCallback = callback;
    }
    
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public void init(long windowHandle) {
        ImGui.createContext();
        
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setConfigWindowsMoveFromTitleBarOnly(true);
        
        // Initialize ImGui for LWJGL
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init();
        
        // Set up style
        ImGui.styleColorsDark();
        setupStyle();
    }

    private void setupStyle() {
        var style = ImGui.getStyle();
        style.setWindowRounding(5.0f);
        style.setFrameRounding(4.0f);
        style.setGrabRounding(3.0f);
        style.setPopupRounding(3.0f);
        style.setScrollbarRounding(3.0f);
        style.setFramePadding(4.0f, 4.0f);
        style.setItemSpacing(8.0f, 4.0f);
        
        // Set colors
        style.setColor(ImGuiCol.WindowBg, 0.1f, 0.1f, 0.1f, 0.9f);
        style.setColor(ImGuiCol.TitleBg, 0.15f, 0.15f, 0.15f, 1.0f);
        style.setColor(ImGuiCol.TitleBgActive, 0.15f, 0.15f, 0.15f, 1.0f);
        style.setColor(ImGuiCol.FrameBg, 0.2f, 0.2f, 0.2f, 0.5f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.3f, 0.3f, 0.3f, 0.4f);
    }

    public void update() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        if (ImGui.isKeyPressed(GLFW.GLFW_KEY_F3)) {
            showDebugWindow = !showDebugWindow;
            showPerformanceWindow = showDebugWindow;  // Toggle movement analysis with F3
        }
        
        if (ImGui.isKeyPressed(GLFW.GLFW_KEY_F4, false)) {  // false means no repeat
            showTankPhysicsDebugger = !showTankPhysicsDebugger;
        }

        if (showDebugWindow) {
            renderDebugWindows();
        }

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void renderDebugWindows() {
        // Movement Analysis Window
        if (showPerformanceWindow) {
            ImGui.setNextWindowSize(320, 600, ImGuiCond.FirstUseEver);  // Set default size
            ImGui.begin("Movement Analysis", ImGuiWindowFlags.None);  // Allow resizing
            
            // Group velocity graphs
            if (ImGui.collapsingHeader("Velocity", ImGuiTreeNodeFlags.DefaultOpen)) {
                float[] velX = getGraphValues("Velocity X");
                float[] velY = getGraphValues("Velocity Y");
                float[] speed = getGraphValues("Speed");
                
                // Initialize empty arrays if null
                if (velX == null) velX = new float[1];
                if (velY == null) velY = new float[1];
                if (speed == null) speed = new float[1];
                
                ImGui.plotLines("##Velocity X", velX, velX.length, 0, "Velocity X", -1000, 1000, 300, 80);
                ImGui.plotLines("##Velocity Y", velY, velY.length, 0, "Velocity Y", -1000, 1000, 300, 80);
                ImGui.plotLines("##Speed", speed, speed.length, 0, "Speed", 0, 1000, 300, 80);
            }
            
            // Group acceleration graphs
            if (ImGui.collapsingHeader("Acceleration", ImGuiTreeNodeFlags.DefaultOpen)) {
                float[] accX = getGraphValues("Acceleration X");
                float[] accY = getGraphValues("Acceleration Y");
                
                // Initialize empty arrays if null
                if (accX == null) accX = new float[1];
                if (accY == null) accY = new float[1];
                
                ImGui.plotLines("##Acceleration X", accX, accX.length, 0, "Acceleration X", -5000, 5000, 300, 80);
                ImGui.plotLines("##Acceleration Y", accY, accY.length, 0, "Acceleration Y", -5000, 5000, 300, 80);
            }
            
            // Group jerk graphs
            if (ImGui.collapsingHeader("Jerk", ImGuiTreeNodeFlags.DefaultOpen)) {
                float[] jerkX = getGraphValues("Jerk X");
                float[] jerkY = getGraphValues("Jerk Y");
                
                // Initialize empty arrays if null
                if (jerkX == null) jerkX = new float[1];
                if (jerkY == null) jerkY = new float[1];
                
                ImGui.plotLines("##Jerk X", jerkX, jerkX.length, 0, "Jerk X", -10000, 10000, 300, 80);
                ImGui.plotLines("##Jerk Y", jerkY, jerkY.length, 0, "Jerk Y", -10000, 10000, 300, 80);
            }
            
            ImGui.end();
        }

        // Entity Debug Window
        if (showEntityDebugger) {
            ImGui.begin("Entity Debugger", ImGuiWindowFlags.AlwaysAutoResize);
            
            for (var entityEntry : entityStates.entrySet()) {
                if (ImGui.treeNode(entityEntry.getKey())) {
                    for (var stateEntry : entityEntry.getValue().entrySet()) {
                        ImGui.text(stateEntry.getKey() + ": " + stateEntry.getValue().toString());
                    }
                    ImGui.treePop();
                }
            }
            
            ImGui.end();
        }

        // Labels Window
        ImGui.begin("Debug Info", ImGuiWindowFlags.AlwaysAutoResize);
        for (var entry : labels.entrySet()) {
            ImGui.text(entry.getKey() + ": " + entry.getValue());
        }
        ImGui.end();

        // Tank Physics Debug Window
        if (showTankPhysicsDebugger) {
            ImGui.begin("Tank Physics Debug", ImGuiWindowFlags.AlwaysAutoResize);
            
            boolean changed = false;
            
            changed |= ImGui.sliderFloat("Acceleration", tankAcceleration, 100.0f, 5000.0f);
            changed |= ImGui.sliderFloat("Box2D Friction", tankFriction, 0.0f, 1.0f);
            changed |= ImGui.sliderFloat("Linear Damping", tankLinearDamping, 0.0f, 1.0f);
            changed |= ImGui.sliderFloat("Angular Damping", tankAngularDamping, 0.0f, 5.0f);
            changed |= ImGui.sliderFloat("Density", tankDensity, 0.1f, 10.0f);
            changed |= ImGui.sliderFloat("Restitution", tankRestitution, 0.0f, 1.0f);
            changed |= ImGui.sliderFloat("Velocity Friction", tankVelocityFriction, 0.9f, 1.0f);
            
            if (changed && tankPhysicsCallback != null) {
                tankPhysicsCallback.onTankPhysicsUpdate(
                    tankAcceleration[0], tankFriction[0],
                    tankLinearDamping[0], tankAngularDamping[0], tankDensity[0],
                    tankRestitution[0], tankVelocityFriction[0]
                );
            }
            
            ImGui.end();
        }
    }
    
    private float[] getGraphValues(String id) {
        List<Float> data = graphs.get(id);
        if (data == null || data.isEmpty()) return null;
        
        float[] values = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            values[i] = data.get(i);
        }
        return values;
    }

    public void addGraphValue(String id, float value) {
        graphs.computeIfAbsent(id, k -> new ArrayList<>()).add(value);
        List<Float> data = graphs.get(id);
        if (data.size() > maxDataPoints) {
            data.remove(0);
        }
    }

    public void setLabel(String id, String text) {
        labels.put(id, text);
    }

    public void updateEntityState(String entityId, String key, Object value) {
        entityStates.computeIfAbsent(entityId, k -> new HashMap<>()).put(key, value);
    }

    public void togglePerformanceWindow() {
        showPerformanceWindow = !showPerformanceWindow;
    }

    public void toggleEntityDebugger() {
        showEntityDebugger = !showEntityDebugger;
    }

    public void toggleTankPhysicsDebugger() {
        showTankPhysicsDebugger = !showTankPhysicsDebugger;
    }

    public void setTankPhysicsValues(float acceleration, float friction,
                                   float linearDamping, float angularDamping, float density,
                                   float restitution) {
        this.tankAcceleration[0] = acceleration;
        this.tankFriction[0] = friction;
        this.tankLinearDamping[0] = linearDamping;
        this.tankAngularDamping[0] = angularDamping;
        this.tankDensity[0] = density;
        this.tankRestitution[0] = restitution;
        this.tankVelocityFriction[0] = 0.95f;
    }

    public void cleanup() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
} 