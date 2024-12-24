package com.ur91k.jdiep.engine.ecs.components.debug;

import com.ur91k.jdiep.engine.ecs.components.base.Component;
import java.util.HashMap;
import java.util.Map;

public class DebugStateComponent extends Component {
    private Map<String, Object> debugValues = new HashMap<>();
    private boolean isVisible = true;
    
    public void setValue(String key, Object value) {
        debugValues.put(key, value);
    }
    
    public Object getValue(String key) {
        return debugValues.get(key);
    }
    
    public Map<String, Object> getValues() {
        return new HashMap<>(debugValues);  // Return a copy for safety
    }
    
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void clear() {
        debugValues.clear();
    }
} 