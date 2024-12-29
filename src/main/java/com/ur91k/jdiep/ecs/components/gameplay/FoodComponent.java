package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;

public class FoodComponent implements Component {
    private int experienceValue;
    private FoodType type;
    
    public FoodComponent() {
        // Default constructor for Ashley's pooling
        this.experienceValue = 0;
        this.type = FoodType.TINY;
    }
    
    public void init(int experienceValue, FoodType type) {
        this.experienceValue = experienceValue;
        this.type = type;
    }
    
    public int getExperienceValue() {
        return experienceValue;
    }
    
    public FoodType getType() {
        return type;
    }
} 