package com.ur91k.jdiep.ecs.components.gameplay;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    private String playerId;  // Unique network ID
    private boolean isLocalPlayer;
    private String playerName;
    
    public PlayerComponent() {
        // Default constructor for Ashley's pooling
        this.playerId = "";
        this.isLocalPlayer = false;
        this.playerName = "";
    }
    
    public void init(String playerId, boolean isLocalPlayer, String playerName) {
        this.playerId = playerId;
        this.isLocalPlayer = isLocalPlayer;
        this.playerName = playerName;
    }
    
    public String getPlayerId() { return playerId; }
    public boolean isLocalPlayer() { return isLocalPlayer; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
} 