package com.ur91k.jdiep.engine.ecs.components;

import com.ur91k.jdiep.engine.ecs.components.base.Component;
import com.ur91k.jdiep.engine.ecs.firing.TurretPhase;
import org.joml.Vector2f;

public class TurretComponent extends Component {
    private float widthRatio;        // Relative to tank diameter (0.0 - 1.0)
    private float lengthRatio;       // Relative to tank diameter
    private Vector2f offsetRatio;    // Position offset relative to tank diameter
    private float radialOffset;      // Angle offset from tank's rotation (radians)
    private TurretPhase phase;       // Firing phase info
    
    public TurretComponent(
        float widthRatio,
        float lengthRatio,
        Vector2f offsetRatio,
        float radialOffset,
        TurretPhase phase
    ) {
        if (widthRatio <= 0 || widthRatio > 1.0f) {
            throw new IllegalArgumentException("Width ratio must be between 0 and 1");
        }
        if (lengthRatio <= 0) {
            throw new IllegalArgumentException("Length ratio must be positive");
        }
        
        this.widthRatio = widthRatio;
        this.lengthRatio = lengthRatio;
        this.offsetRatio = new Vector2f(offsetRatio);
        this.radialOffset = radialOffset;
        this.phase = phase;
    }
    
    // Actual dimensions based on tank diameter
    public float getWidth(float tankDiameter) {
        return tankDiameter * widthRatio;
    }
    
    public float getLength(float tankDiameter) {
        return tankDiameter * lengthRatio;
    }
    
    public Vector2f getOffset(float tankDiameter) {
        return new Vector2f(offsetRatio).mul(tankDiameter);
    }
    
    // Base properties
    public float getWidthRatio() { return widthRatio; }
    public float getLengthRatio() { return lengthRatio; }
    public Vector2f getOffsetRatio() { return new Vector2f(offsetRatio); }
    public float getRadialOffset() { return radialOffset; }
    public TurretPhase getPhase() { return phase; }
    
    // World position calculation (considering tank position, rotation, and offsets)
    public Vector2f getWorldPosition(Vector2f tankPosition, float tankRotation, float tankDiameter) {
        float totalRotation = tankRotation + radialOffset;
        Vector2f offset = getOffset(tankDiameter);
        
        // Rotate offset by tank rotation
        float cos = (float)Math.cos(totalRotation);
        float sin = (float)Math.sin(totalRotation);
        float rotatedX = offset.x * cos - offset.y * sin;
        float rotatedY = offset.x * sin + offset.y * cos;
        
        return new Vector2f(tankPosition).add(rotatedX, rotatedY);
    }
} 