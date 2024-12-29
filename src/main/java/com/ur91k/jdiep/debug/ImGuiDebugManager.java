package com.ur91k.jdiep.debug;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
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
        }

        if (showDebugWindow) {
            renderDebugWindows();
        }

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void renderDebugWindows() {
        // Performance Graphs Window
        if (showPerformanceWindow) {
            ImGui.begin("Performance", ImGuiWindowFlags.AlwaysAutoResize);
            
            for (var entry : graphs.entrySet()) {
                String label = entry.getKey();
                List<Float> data = entry.getValue();
                
                if (!data.isEmpty()) {
                    float[] values = new float[data.size()];
                    for (int i = 0; i < data.size(); i++) {
                        values[i] = data.get(i);
                    }
                    ImGui.plotLines(label, values, values.length, 0, 
                        null, Float.MIN_VALUE, Float.MAX_VALUE, 
                        200, 80);
                }
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

    public void cleanup() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
} 